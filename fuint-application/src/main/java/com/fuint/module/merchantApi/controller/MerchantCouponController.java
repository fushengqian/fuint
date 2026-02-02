package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.ReqCouponDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.param.CouponListParam;
import com.fuint.common.param.CouponReceiveParam;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商户卡券接口controller
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

    @ApiOperation(value = "获取卡券列表")
    @RequestMapping(value = "/couponList", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject couponList(HttpServletRequest request, @RequestBody CouponListParam params) {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        params.setMerchantId(merchantId);
        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            params.setStoreId(staff.getStoreId());
        }
        params.setStatus(null);
        ResponseObject result = couponService.findCouponList(params);
        return getSuccessResult(result.getData());
    }

    @ApiOperation(value = "保存卡券信息")
    @RequestMapping(value = "/saveCoupon", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveCoupon(HttpServletRequest request, @RequestBody ReqCouponDto reqCouponDto) {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null || userInfo.getMobile() == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        MtStaff staff = staffService.queryStaffByMobile(userInfo.getMobile());
        MtCoupon couponInfo = couponService.queryCouponById(reqCouponDto.getId());
        if (staff == null || couponInfo == null || !merchantId.equals(couponInfo.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        BeanUtils.copyProperties(reqCouponDto, couponInfo);
        couponService.updateById(couponInfo);
        return getSuccessResult(couponInfo);
    }

    @ApiOperation(value = "发放卡券")
    @RequestMapping(value = "/sendCoupon", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject sendCoupon(HttpServletRequest request, @RequestBody CouponReceiveParam receiveParam) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null || userInfo.getMobile() == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        MtStaff staff = staffService.queryStaffByMobile(userInfo.getMobile());
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
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
        ResponseObject result = couponService.sendCoupon(receiveParam.getCouponId(), receiveParam.getUserId(), receiveParam.getNum(), true, null, staff.getRealName());
        if (!result.getCode().equals(200)) {
            return getFailureResult(result.getCode(), result.getMessage());
        }
        return getSuccessResult(true);
    }
}
