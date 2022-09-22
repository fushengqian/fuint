package com.fuint.application.web.rest;

import com.fuint.application.enums.StatusEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.staff.StaffService;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 核销卡券controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/confirm")
public class ConfirmController extends BaseController {

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
    private StaffService staffService;

    /**
     * 核销卡券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String code = param.get("code") == null ? "" : param.get("code").toString();
        String amount = (param.get("amount") == null || param.get("amount") == "") ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();

        if (StringUtil.isEmpty(token)) {
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

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());

        // 员工是否已经被审核
        HashMap params = new HashMap<>();
        params.put("EQ_userId", mtUser.getId().toString());
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStaff> confirmerList = staffService.queryStaffByParams(params);
        Integer storeId = 0;
        if (confirmerList.size() > 0) {
            for (MtStaff staff : confirmerList) {
                if (!staff.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                    return getFailureResult(1003, "员工状态异常！");
                }

                if (staff.getStoreId() > 0) {
                    storeId = staff.getStoreId();
                }

                String storeIdsStr = couponInfo.getStoreIds();
                if (StringUtil.isNotEmpty(storeIdsStr)) {
                    String[] storeIds = couponInfo.getStoreIds().split(",");
                    Boolean isSameStore = false;
                    for (String hid : storeIds) {
                        if (staff.getStoreId().toString().equals(hid)) {
                            isSameStore = true;
                            break;
                        }
                    }
                    if (!isSameStore) {
                        return getFailureResult(1003, "员工对该卡券没有权限");
                    }
                }
            }
        } else {
            return getFailureResult(1003, "员工状态异常！");
        }

        Integer userCouponId = userCoupon.getId();
        String confirmCode = "";

        try {
            confirmCode = couponService.useCoupon(userCouponId, mtUser.getId(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(1003, e.getMessage());
        }

        // 获取最新余额
        MtUserCoupon userCouponNew = userCouponRepository.findOne(userCoupon.getId());

        // 组织返回参数
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("result", true);
        result.put("money", couponInfo.getAmount());
        result.put("balance", userCouponNew.getBalance());
        String tips = "";
        result.put("tips", tips);
        result.put("name", couponInfo.getName());
        result.put("code", confirmCode);
        result.put("status", userCouponNew.getStatus());

        return getSuccessResult(result);
    }
}
