package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.*;
import com.fuint.common.param.CouponReceiveParam;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 卡券接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-卡券相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/coupon")
public class MerchantCouponController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/sendCoupon", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject sendCoupon(HttpServletRequest request, @RequestBody CouponReceiveParam receiveParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001);
        }

        MtStaff staff = null;
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (mtUser != null && mtUser.getMobile() != null) {
            staff = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staff == null) {
            return getFailureResult(201, "该账号不是商户");
        }
        if (!merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        // 判断店铺权限
        MtCoupon couponInfo = couponService.queryCouponById(receiveParam.getCouponId());
        if (StringUtil.isNotEmpty(couponInfo.getStoreIds()) && staff.getStoreId() != null && staff.getStoreId() > 0) {
            String[] storeIds = couponInfo.getStoreIds().split(",");
            Boolean isSameStore = false;
            for (String hid : storeIds) {
                if (staff.getStoreId().toString().equals(hid)) {
                    isSameStore = true;
                    break;
                }
            }
            if (!isSameStore) {
                return getFailureResult(1003, "抱歉，该卡券存在店铺使用范围限制，您所在的店铺无发券权限！");
            }
        }
        couponService.sendCoupon(receiveParam.getCouponId(), receiveParam.getUserId(), receiveParam.getNum(), true, null, staff.getRealName());
        return getSuccessResult(true);
    }
}
