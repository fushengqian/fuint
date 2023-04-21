package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.UserCouponDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
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
@RequestMapping(value = "/backendApi/doConfirm")
public class BackendDoConfirmController extends BaseController {

    /**
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 账户服务接口
     */
    @Autowired
    private AccountService accountService;

    /**
     * 核销记录服务接口
     * */
    @Autowired
    private ConfirmLogService confirmLogService;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 核销详情
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = param.get("id") == null ? "" : param.get("id").toString();
        String userCouponCode = param.get("code") == null ? "" : param.get("code").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

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

        String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");

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
        CouponTypeEnum[] typeListEnum = CouponTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CouponTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("couponInfo", userCouponInfo);
        result.put("userInfo", userInfo);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 确认核销
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = param.get("userCouponId") == null ? "" : param.get("userCouponId").toString();
        String amount = (param.get("amount") == null || StringUtil.isEmpty(param.get("amount").toString())) ? "0" : param.get("amount").toString();
        String remark = (param.get("remark") == null || StringUtil.isEmpty(param.get("remark").toString())) ? "后台核销" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "系统参数有误");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        MtUserCoupon mtUserCoupon = mtUserCouponMapper.selectById(Integer.parseInt(userCouponId));
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) && StringUtil.isEmpty(amount)) {
            return getFailureResult(201, "储值卡核销金额不能为空");
        }

        try {
            couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, "核销失败：" + e.getMessage());
        }

        return getSuccessResult(true);
    }
}
