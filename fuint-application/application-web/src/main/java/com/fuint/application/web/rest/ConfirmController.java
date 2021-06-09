package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.entities.MtConfirmer;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.confirmer.ConfirmerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**-
 * 核销卡券controller
 * Created by zach on 2019/9/05.
 */
@RestController
@RequestMapping(value = "/rest/confirm")
public class ConfirmController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmController.class);

    /**
     * 卡券服务接口
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

    /**
     * 核销卡券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String code = request.getParameter("code") == null ? "" : request.getParameter("code");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(401);
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
                        return getFailureResult(1003, "核销人员对该卡券没有权限");
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

        // 组织返回参数
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("result", true);
        result.put("money", couponInfo.getAmount());
        String tips = "";
        result.put("tips", tips);
        result.put("name", couponInfo.getName());
        result.put("code", cCode);

        return getSuccessResult(result);
    }
}
