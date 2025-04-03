package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.UserCouponDto;
import com.fuint.common.enums.CouponExpireTypeEnum;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.ConfirmLogService;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.repository.model.TAccount;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券核销类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-卡券核销相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/doConfirm")
public class BackendDoConfirmController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 账户服务接口
     */
    private AccountService accountService;

    /**
     * 核销记录服务接口
     * */
    private ConfirmLogService confirmLogService;

    /**
     * 核销详情
     *
     * @param param 详情参数
     * @return
     */
    @ApiOperation(value = "核销详情")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:confirm:index')")
    public ResponseObject info(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String userCouponId = param.get("id") == null ? "" : param.get("id").toString();
        String userCouponCode = param.get("code") == null ? "" : param.get("code").toString();

        if (StringUtil.isEmpty(userCouponCode) && StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "核销券码不能为空");
        }

        // 通过券码或ID获取
        MtUserCoupon userCoupon;
        if (!StringUtil.isEmpty(userCouponCode)) {
            userCoupon = mtUserCouponMapper.findByCode(userCouponCode);
        } else {
            userCoupon = mtUserCouponMapper.selectById(Integer.parseInt(userCouponId));
        }

        if (userCoupon == null) {
            return getFailureResult(201, "未查询到该卡券信息");
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
        MtUser userInfo = memberService.queryMemberById(userCoupon.getUserId());

        String effectiveDate = "";
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
            effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd HH:mm");
        }
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FLEX.getKey())) {
            effectiveDate = DateUtil.formatDate(userCoupon.getCreateTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(userCoupon.getExpireTime(), "yyyy.MM.dd HH:mm");
        }

        UserCouponDto userCouponInfo = new UserCouponDto();
        userCouponInfo.setName(couponInfo.getName());
        userCouponInfo.setEffectiveDate(effectiveDate);
        userCouponInfo.setDescription(couponInfo.getDescription());
        userCouponInfo.setId(userCoupon.getId());
        userCouponInfo.setType(couponInfo.getType());
        userCouponInfo.setStatus(userCoupon.getStatus());
        userCouponInfo.setBalance(userCoupon.getBalance());
        userCouponInfo.setAmount(userCoupon.getAmount());
        userCouponInfo.setCode(userCoupon.getCode());
        userCouponInfo.setUseRule(couponInfo.getOutRule());
        Long confirmCount = confirmLogService.getConfirmNum(userCoupon.getId());
        userCouponInfo.setConfirmCount(confirmCount.intValue());

        // 卡券类型列表
        List<ParamDto> typeList = CouponTypeEnum.getCouponTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("couponInfo", userCouponInfo);
        result.put("userInfo", userInfo);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 确认核销
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "确认核销")
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:confirm:index')")
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = param.get("userCouponId") == null ? "" : param.get("userCouponId").toString();
        String amount = (param.get("amount") == null || StringUtil.isEmpty(param.get("amount").toString())) ? "0" : param.get("amount").toString();
        String remark = (param.get("remark") == null || StringUtil.isEmpty(param.get("remark").toString())) ? "后台核销" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "系统参数有误");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();

        MtUserCoupon mtUserCoupon = mtUserCouponMapper.selectById(Integer.parseInt(userCouponId));
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) && StringUtil.isEmpty(amount)) {
            return getFailureResult(201, "储值卡核销金额不能为空");
        }

        couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId(), storeId, 0, new BigDecimal(amount), remark);
        return getSuccessResult(true);
    }
}
