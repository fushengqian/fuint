package com.fuint.coupon.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtCoupon;
import com.fuint.coupon.dao.entities.MtUserCoupon;
import com.fuint.coupon.dao.entities.MtConfirmer;
import com.fuint.coupon.dao.repositories.MtUserCouponRepository;
import com.fuint.coupon.service.coupon.CouponService;
import com.fuint.coupon.service.member.MemberService;
import com.fuint.coupon.service.token.TokenService;
import com.fuint.coupon.service.confirmer.ConfirmerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fuint.coupon.BaseController;
import com.fuint.coupon.ResponseObject;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fuint.coupon.dao.entities.MtUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**-
 * 核销优惠券controller
 * Created by zach on 2019/9/05.
 */
@RestController
@RequestMapping(value = "/rest/confirm")
public class ConfirmController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmController.class);

    /**
     * 优惠券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private ConfirmerService confirmerService;

    @Autowired
    private MemberService memberService;

    /**
     * 核销优惠券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    public ResponseObject doConfirm(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("token");
        String code = request.getParameter("code") == null ? "" : request.getParameter("code");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = userCouponRepository.findByCode(code);
        if (null == userCoupon) {
            return getFailureResult(1003, "该券不存在！");
        }

        // 券码已过期
        if (couponService.codeExpired(code)) {
            return getFailureResult(1003, "二维码已过期，请重新获取！");
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId().longValue());

        // 核销人员是否已经被审核
        HashMap params = new HashMap<>();
        params.put("EQ_userId", mtUser.getId().toString());
        params.put("EQ_status", "A");
        List<MtConfirmer> confirmerList = confirmerService.queryConfirmerByParams(params);
        Integer storeId = 0;
        if (confirmerList.size() > 0) {
            for (MtConfirmer wp : confirmerList) {
                if (!wp.getAuditedStatus().equals("A")) {
                    return getFailureResult(1003, "核销人员状态异常！");
                }

                if (wp.getStoreId() > 0) {
                    storeId = wp.getStoreId();
                }

                String storeIdsStr = couponInfo.getStoreIds();
                if (StringUtils.isNotEmpty(storeIdsStr)) {
                    String[] storeIds = couponInfo.getStoreIds().split(",");
                    Boolean isSameStore = false;
                    for (String hid : storeIds) {
                        if (wp.getStoreId().toString().equals(hid)) {
                            isSameStore = true;
                            break;
                        }
                    }
                    if (!isSameStore) {
                        return getFailureResult(1003, "核销人员对该优惠券没有权限");
                    }
                }
            }
        } else {
            return getFailureResult(1003, "核销人员状态异常！");
        }

        Integer userCouponId = userCoupon.getId();
        String cCode = "";

        try {
            cCode = couponService.useCoupon(userCouponId.longValue(), mtUser.getId(), storeId);
        } catch (BusinessCheckException e) {
            return getFailureResult(1003, e.getMessage());
        }

        //组织返回参数
        ResponseObject responseObject;
        Map<String, Object> outparams = new HashMap<String, Object>();
        outparams.put("result", true);
        outparams.put("money", couponInfo.getMoney());
        String tips = "";
        outparams.put("tips", tips);
        outparams.put("name", couponInfo.getName());
        outparams.put("code", cCode);

        responseObject = getSuccessResult(outparams);

        return getSuccessResult(responseObject.getData());
    }
}
