package com.fuint.module.clientApi.controller;

import com.alipay.api.AlipayApiException;
import com.fuint.common.bean.WxPayBean;
import com.fuint.common.dto.*;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.ijpay.alipay.AliPayApi;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-支付相关接口")
@RestController
@RequestMapping(value = "/clientApi/pay")
public class ClientPayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientPayController.class);

    @Autowired
    WxPayBean wxPayBean;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 支付宝服务接口
     * */
    @Autowired
    private AlipayService alipayService;

    /**
     * 支付服务接口
     * */
    @Autowired
    private PaymentService paymentService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    UserGradeService userGradeService;

    /**
     * 支付前查询
     * */
    @RequestMapping(value = "/prePay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject prePay(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String useFor = request.getParameter("type") == null ? "" : request.getParameter("type");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(201, "请先登录");
        }
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        Map<String, Object> outParams = new HashMap<>();

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.POINT.getKey());
        String canUsedAsMoney = "false";
        String exchangeNeedPoint = "0";
        for (MtSetting setting : settingList) {
            if (setting.getName().equals("canUsedAsMoney")) {
                canUsedAsMoney = setting.getValue();
            } else if (setting.getName().equals("exchangeNeedPoint")) {
                exchangeNeedPoint = setting.getValue();
            }
        }

        // 可用卡券
        CouponDto canUseCouponInfo = null;
        if (mtUser != null) {
            List<CouponDto> couponList = userCouponService.getPayAbleCouponList(mtUser.getId(), useFor);
            if (couponList.size() > 0) {
                canUseCouponInfo = couponList.get(0);
            }
        }

        // 会员折扣
        BigDecimal payDiscount = new BigDecimal("1");
        if (mtUser != null) {
            MtUserGrade userGrade = userGradeService.queryUserGradeById(Integer.parseInt(mtUser.getGradeId()));
            if (userGrade != null) {
                if (userGrade.getDiscount() > 0) {
                    payDiscount = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"), BigDecimal.ROUND_CEILING, 2);
                }
            }
        }

        // 可用积分
        Integer canUsePointAmount = 0;
        if (mtUser != null && canUsedAsMoney.equals("true")) {
            canUsePointAmount = mtUser.getPoint();
        }

        outParams.put("canUsedAsMoney", canUsedAsMoney);
        outParams.put("exchangeNeedPoint", exchangeNeedPoint);
        outParams.put("canUsePointAmount", canUsePointAmount);
        outParams.put("canUseCouponInfo", canUseCouponInfo);
        outParams.put("canUseCouponInfo", canUseCouponInfo);
        outParams.put("payDiscount", payDiscount);

        return getSuccessResult(outParams);
    }

    /**
     * 请求支付
     * */
    @RequestMapping(value = "/doPay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doPay(HttpServletRequest request) {
        try {
            Map<String, Object> result = paymentService.doPay(request);
            return getSuccessResult(result);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage() == null ? "订单支付出错" : e.getMessage());
        }
    }

    /**
     * 微信支付回调
     */
    @RequestMapping(value = "/weixinCallback", method = RequestMethod.POST)
    @CrossOrigin
    public void weixinCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("微信支付结果回调....");

        Map<String, String> resData = weixinService.processResXml(request);
        logger.info("微信返回Map:" + resData);
        if (!CollectionUtils.isEmpty(resData)) {
            String orderSn = resData.get("out_trade_no"); // 商户订单号
            String orderId = resData.get("transaction_id"); // 微信交易单号
            String tranAmt = resData.get("total_fee"); // 交易金额
            BigDecimal tranAmount = new BigDecimal(tranAmt).divide(new BigDecimal("100"), BigDecimal.ROUND_CEILING, 2);
            // 参数校验
            if (StringUtil.isNotEmpty(orderSn) && StringUtil.isNotEmpty(tranAmt) && StringUtil.isNotEmpty(orderId)) {
                UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
                if (orderInfo != null) {
                    String result = resData.get("return_code");
                    if (!result.equals("SUCCESS")) {
                        logger.error("微信支付回调接口验签失败");
                        return;
                    }
                    // 订单金额
                    BigDecimal payAmount = orderInfo.getPayAmount();
                    int compareFlag = tranAmount.compareTo(payAmount);
                    if (compareFlag == 0) { // 支付金额正确
                        if (orderInfo.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                            boolean flag = paymentService.paymentCallback(orderInfo);
                            logger.info("回调结果：" + flag);
                            if (flag) {
                                weixinService.processRespXml(response, true);
                            } else {
                                weixinService.processRespXml(response, false);
                            }
                        } else {
                            logger.error("订单{}已经支付，orderInfo.getStatus() = {}, CREATED.getKey() = {}", orderSn, orderInfo.getStatus(), OrderStatusEnum.CREATED.getKey());
                        }
                    } else {
                        logger.error("回调金额与支付金额不匹配 tranAmount = {}, payAmount = {}, compareFlag = {}", tranAmount, orderInfo.getPayAmount(), compareFlag);
                    }
                } else {
                    logger.error("支付订单{}对应的信息不存在", orderSn);
                }
            }
        }
    }

    /**
     * 支付宝支付回调
     */
    @RequestMapping(value = "/aliPayCallback", method = RequestMethod.POST)
    @CrossOrigin
    public String aliPayCallback(HttpServletRequest request) throws Exception {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = AliPayApi.toMap(request);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                logger.info("{} = {}", entry.getKey(), entry.getValue());
            }
            String orderSn = params.get("out_trade_no") != null ? params.get("out_trade_no") : "";
            if (StringUtil.isEmpty(orderSn)) {
                logger.error("支付宝验证失败 订单号为空");
            }
            Boolean verifyResult = alipayService.checkCallBack(params);
            if (verifyResult) {
                logger.info("支付宝验证成功 succcess");
                UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
                Boolean flag = paymentService.paymentCallback(orderInfo);
                if (flag) {
                    return "success";
                } else {
                    return "failure";
                }
            } else {
                logger.error("支付宝验证失败 orderSn={}", orderSn);
                return "failure";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝回调出错啦...");
            return "failure";
        }
    }
}
