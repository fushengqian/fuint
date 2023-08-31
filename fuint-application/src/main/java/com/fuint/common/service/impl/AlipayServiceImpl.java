package com.fuint.common.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fuint.common.bean.AliPayBean;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 支付宝相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    @Autowired
    AliPayBean aliPayBean;

    /**
     * 创建支付订单
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException {
        logger.info("AlipayService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);

        String goodsInfo = orderInfo.getOrderSn();
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            goodsInfo = OrderTypeEnum.PRESTORE.getValue();
        }

        // 更新支付金额
        BigDecimal payAmount1 = new BigDecimal(payAmount).divide(new BigDecimal("100"));
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setPayAmount(payAmount1);
        reqDto.setPayType(orderInfo.getPayType());
        orderService.updateOrder(reqDto);

        getApiConfig(orderInfo.getStoreId());
        String notifyUrl = aliPayBean.getDomain();
        AlipayTradePayModel model = new AlipayTradePayModel();
        model.setAuthCode(authCode);
        model.setSubject(goodsInfo);
        model.setTotalAmount(payAmount1.toString());
        model.setOutTradeNo(orderInfo.getOrderSn());
        model.setStoreId(orderInfo.getStoreId().toString());
        model.setScene("bar_code");

        String code = "";
        try {
            AlipayTradePayResponse response = AliPayApi.tradePayToResponse(model, notifyUrl);
            code = response.getCode();
            String msg = response.getMsg();
            logger.info("AlipayService createPrepayOrder return code: {}, msg ", code, msg);
            if (!code.equals("10000") || !msg.equalsIgnoreCase("Success")) {
                if (code.equals("10003")) {
                    // 需要会员输入支付密码，等待10秒后查询订单
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> payResult = queryPaidOrder(orderInfo.getStoreId(), response.getTradeNo(), orderInfo.getOrderSn());
                    if (payResult == null) {
                        throw new BusinessCheckException("支付宝支付失败");
                    }
                } else {
                    throw new BusinessCheckException("支付宝支付出错：" + msg);
                }
            }
        } catch (Exception e) {
            logger.error("AlipayService createPrepayOrder exception {}", e.getMessage());
            throw new BusinessCheckException("支付宝支付出错，请检查配置项");
        }

        Map<String, String> respData = new HashMap<>();
        respData.put("result", code);

        ResponseObject responseObject = new ResponseObject(200, "支付宝支付接口返回成功", respData);
        logger.info("AlipayService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    @Override
    public Boolean checkCallBack(Map<String, String> params) throws Exception {
        String orderSn = params.get("out_trade_no") != null ? params.get("out_trade_no") : "";
        Integer storeId = 0;
        UserOrderDto orderDto = orderService.getOrderByOrderSn(orderSn);
        if (orderDto != null && orderDto.getStoreInfo() != null) {
            storeId = orderDto.getStoreInfo().getId();
        }
        getApiConfig(storeId);
        return AlipaySignature.rsaCheckV1(params, aliPayBean.getPublicKey(), "UTF-8", "RSA2");
    }

    /**
     * 获取支付配置
     * */
    public AliPayApiConfig getApiConfig(Integer storeId) throws BusinessCheckException {
        AliPayApiConfig aliPayApiConfig;
        String appId = aliPayBean.getAppId();
        String privateKey = aliPayBean.getPrivateKey();
        String publicKey = aliPayBean.getPublicKey();

        // 优先读取店铺的支付账号
        MtStore mtStore = storeService.queryStoreById(storeId);
        if (mtStore != null && StringUtil.isNotEmpty(mtStore.getAlipayAppId()) && StringUtil.isNotEmpty(mtStore.getAlipayPrivateKey()) && StringUtil.isNotEmpty(mtStore.getAlipayPublicKey())) {
            appId = mtStore.getAlipayAppId();
            privateKey = mtStore.getAlipayPrivateKey();
            publicKey = mtStore.getAlipayPublicKey();
        }

        aliPayApiConfig = AliPayApiConfig.builder()
                .setAppId(appId)
                .setAliPayPublicKey(publicKey)
                .setCharset("UTF-8")
                .setPrivateKey(privateKey)
                .setServiceUrl(aliPayBean.getServerUrl())
                .setSignType("RSA2")
                .build();

        AliPayApiConfigKit.setThreadLocalAppId(appId);
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);

        return aliPayApiConfig;
    }

    /**
     * 查询支付订单
     * */
    @Override
    public Map<String, String> queryPaidOrder(Integer storeId, String tradeNo, String orderSn) throws BusinessCheckException {
        try {
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            if (StringUtil.isNotEmpty(orderSn)) {
                model.setOutTradeNo(orderSn);
            }
            if (StringUtil.isNotEmpty(tradeNo)) {
                model.setTradeNo(tradeNo);
            }
            getApiConfig(storeId);
            AlipayTradeQueryResponse response = AliPayApi.tradeQueryToResponse(model);
            if (response != null) {
                // TradeStatus：TRADE_SUCCESS（交易支付成功，可进行退款）或 TRADE_FINISHED（交易结束，不可退款）
                if (response.getTradeStatus() != null && response.getTradeStatus().equals("TRADE_SUCCESS")) {
                    Map<String, String> result = new HashMap<>();
                    result.put("tradeNo", response.getTradeNo());
                    result.put("status", response.getTradeStatus());
                    result.put("payAmount", response.getBuyerPayAmount());
                    return result;
                }
            }
        } catch (AlipayApiException e) {
            logger.info("AlipayService queryPaidOrder response", e.getMessage());
        }

        return null;
    }

    /**
     * 支付宝发起退款
     *
     * @param storeId
     * @param orderSn
     * @param totalAmount
     * @param refundAmount
     * @param platform
     * @return
     * */
    public Boolean doRefund(Integer storeId, String orderSn, BigDecimal totalAmount, BigDecimal refundAmount, String platform) throws BusinessCheckException {
        try {
            logger.info("AlipayService.doRefund orderSn = {}, totalFee = {}, refundFee = {}", orderSn, totalAmount, refundAmount);
            if (StringUtil.isEmpty(orderSn)) {
                throw new BusinessCheckException("退款订单号不能为空...");
            }
            if (refundAmount.compareTo(totalAmount) > 0) {
                throw new BusinessCheckException("退款金额不能大于总金额...");
            }
            getApiConfig(storeId);
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(orderSn);
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason("申请退款");
            AlipayTradeRefundResponse refundResponse = AliPayApi.tradeRefundToResponse(model);
            String code = refundResponse.getCode();
            String msg = refundResponse.getMsg();
            String subMsg = refundResponse.getSubMsg() == null ? msg : refundResponse.getSubMsg();
            logger.info("AlipayService refundResult response Body = {}", refundResponse.getBody());
            if (!code.equals("10000") || !msg.equalsIgnoreCase("Success")) {
                throw new BusinessCheckException("支付宝退款失败，" + subMsg);
            }
        } catch (AlipayApiException e) {
            logger.error("AlipayService.doRefund error = {}", e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
