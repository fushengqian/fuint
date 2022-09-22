package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.dto.ParamDto;
import com.fuint.application.dto.UserCouponDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.service.account.TAccountService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
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
    private TAccountService accountService;

    /**
     * 核销记录服务接口
     * */
    @Autowired
    private ConfirmLogService confirmLogService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

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

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        if (StringUtil.isEmpty(userCouponCode) && StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "核销券码不能为空");
        }

        // 通过券码或ID获取
        MtUserCoupon userCoupon;
        if (!StringUtil.isEmpty(userCouponCode)) {
            userCoupon = userCouponRepository.findByCode(userCouponCode);
        } else {
            userCoupon = userCouponRepository.findOne(Integer.parseInt(userCouponId));
        }

        if (userCoupon == null) {
            return getFailureResult(201, "未查询到该卡券信息");
        }

        if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            return getFailureResult(201, "该卡券可能已经核销或过期，请确认");
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

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }
        if (StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "系统参数有误");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        MtUserCoupon mtUserCoupon = userCouponRepository.findOne(Integer.parseInt(userCouponId));
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) && StringUtil.isEmpty(amount)) {
            return getFailureResult(201, "预存卡核销金额不能为空");
        }

        try {
            couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId().intValue(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, "核销失败：" + e.getMessage());
        }

        return getSuccessResult(true);
    }
}
