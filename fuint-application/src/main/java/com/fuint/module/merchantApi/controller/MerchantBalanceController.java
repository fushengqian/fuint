package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.param.RechargeParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 余额接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/balance")
public class MerchantBalanceController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody RechargeParam rechargeParam) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String isWechat = request.getHeader("isWechat") == null ? "" : request.getHeader("isWechat");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001);
        }

        String rechargeAmount = rechargeParam.getRechargeAmount() == null ? "" : rechargeParam.getRechargeAmount();
        String customAmount = rechargeParam.getCustomAmount() == null ? "" : rechargeParam.getCustomAmount();
        if (StringUtil.isEmpty(rechargeAmount) && StringUtil.isEmpty(customAmount)) {
            return getFailureResult(2000, "请确认充值金额");
        }

        Integer merchantId = merchantService.getMerchantId(merchantNo);

        // 充值赠送金额
        String ruleParam = "";
        MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.BALANCE.getKey(), BalanceSettingEnum.RECHARGE_RULE.getKey());
        if (StringUtil.isNotEmpty(rechargeAmount) && mtSetting != null) {
            if (mtSetting.getValue() != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
                String rules[] = mtSetting.getValue().split(",");
                for (String rule : rules) {
                     String amountArr[] = rule.split("_");
                     if (amountArr.length == 2) {
                         if (amountArr[0].equals(rechargeAmount)) {
                             ruleParam = rule;
                             break;
                         }
                     }
                }
            }
        }

        // 自定义充值没有赠送金额
        if (StringUtil.isNotEmpty(customAmount) && Integer.parseInt(customAmount) > 0 && (StringUtil.isEmpty(rechargeAmount) || Integer.parseInt(rechargeAmount) <= 0)) {
            rechargeAmount = customAmount;
            ruleParam = customAmount + "_0";
        }

        if (StringUtil.isEmpty(ruleParam)) {
            ruleParam = rechargeAmount + "_0";
        }

        BigDecimal amount = new BigDecimal(rechargeAmount);
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            return getFailureResult(201, "请确认充值金额");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setType(OrderTypeEnum.RECHARGE.getKey());
        orderDto.setUserId(userInfo.getId());
        orderDto.setStoreId(storeId);
        orderDto.setAmount(amount);
        orderDto.setUsePoint(0);
        orderDto.setRemark("会员充值");
        orderDto.setParam(ruleParam);
        orderDto.setStatus(OrderStatusEnum.CREATED.getKey());
        orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setOrderMode("");
        orderDto.setCouponId(0);
        orderDto.setPlatform(platform);
        orderDto.setMerchantId(merchantId);

        MtOrder orderInfo = orderService.saveOrder(orderDto);

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal pay = amount.multiply(new BigDecimal("100"));
        orderInfo.setPayType(PayTypeEnum.JSAPI.getKey());
        ResponseObject paymentInfo = paymentService.createPrepayOrder(mtUser, orderInfo, (pay.intValue()), "", 0, ip, platform, isWechat);
        if (paymentInfo.getData() == null) {
            return getFailureResult(201, "抱歉，发起支付失败");
        }

        Object payment = paymentInfo.getData();

        Map<String, Object> data = new HashMap();
        data.put("payment", payment);
        data.put("orderInfo", orderInfo);

        return getSuccessResult(data);
    }
}
