package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.param.ConfirmParam;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡券核销controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-卡券核销相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/confirm")
public class ClientConfirmController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 员工服务接口
     * */
    private StaffService staffService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 核销卡券
     */
    @ApiOperation(value = "核销卡券")
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(@RequestBody ConfirmParam confirmParam) {
        String code = confirmParam.getCode() == null ? "" : confirmParam.getCode();
        String amount = (confirmParam.getAmount() == null || confirmParam.getAmount() == "") ? "0" : confirmParam.getAmount();
        String remark = confirmParam.getRemark() == null ? "" : confirmParam.getRemark();

        UserInfo loginInfo = TokenUtil.getUserInfo();
        if (loginInfo == null) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = mtUserCouponMapper.findByCode(code);
        if (userCoupon == null) {
            return getFailureResult(1003, "该卡券不存在！");
        }

        if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            return getFailureResult(1003, "该卡券状态异常！");
        }

        // 券码已过期
        if (couponService.codeExpired(code)) {
            return getFailureResult(1003, "二维码已过期，请重新获取！");
        }

        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());

        // 同商户判断
        if (!userCoupon.getMerchantId().equals(mtUser.getMerchantId())) {
            return getFailureResult(1003, "不同商户，无核销权限！");
        }

        // 员工是否已经被审核
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staffInfo == null || staffInfo.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
            return getFailureResult(1003, "员工状态异常！");
        }

        String storeIdsStr = couponInfo.getStoreIds();
        if (StringUtil.isNotEmpty(storeIdsStr)) {
            String[] storeIds = couponInfo.getStoreIds().split(",");
            Boolean isSameStore = false;
            for (String sid : storeIds) {
                if (staffInfo.getStoreId().toString().equals(sid)) {
                    isSameStore = true;
                    break;
                }
            }
            if (!isSameStore) {
                return getFailureResult(1003, "抱歉，该卡券有店铺限制，您所在的店铺无法核销！");
            }
        }

        Integer userCouponId = userCoupon.getId();
        String confirmCode = "";

        try {
            confirmCode = couponService.useCoupon(userCouponId, mtUser.getId(), staffInfo.getStoreId(), 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(1003, e.getMessage());
        }

        // 获取最新余额
        MtUserCoupon userCouponNew = mtUserCouponMapper.selectById(userCoupon.getId());

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("money", couponInfo.getAmount());
        result.put("balance", userCouponNew.getBalance());
        result.put("tips", "");
        result.put("name", couponInfo.getName());
        result.put("code", confirmCode);
        result.put("status", userCouponNew.getStatus());

        return getSuccessResult(result);
    }
}
