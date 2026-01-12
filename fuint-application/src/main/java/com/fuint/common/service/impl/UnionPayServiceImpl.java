package com.fuint.common.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ijpay.unionpay.UnionPayApi;
import com.fuint.common.bean.UnionPayBean;
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
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.unionpay.enums.ServiceEnum;
import com.ijpay.unionpay.model.MicroPayModel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 云闪付相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class UnionPayServiceImpl implements UnionPayService {

    private static final Logger logger = LoggerFactory.getLogger(UnionPayServiceImpl.class);

    private UnionPayBean unionPayBean;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 创建预支付订单
     *
     * @param userInfo 会员信息
     * @param orderInfo 订单信息
     * @param payAmount 支付金额
     * @param authCode 付款码
     * @param giveAmount 赠送金额
     * @param ip 支付IP地址
     * @param platform 支付平台
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException {
        logger.info("UnionPayService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);

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
        reqDto.setOrderSn(orderInfo.getOrderSn());
        orderService.updateOrder(reqDto);

        getApiConfig(orderInfo.getStoreId());
        Map<String, String> params = MicroPayModel.builder()
                .service(ServiceEnum.MICRO_PAY.toString())
                .mch_id(unionPayBean.getMachId())
                .out_trade_no(WxPayKit.generateStr())
                .body(goodsInfo)
                .attach("云闪付支付")
                .total_fee(payAmount1.toString())
                .mch_create_ip(ip)
                .auth_code(authCode)
                .nonce_str(WxPayKit.generateStr())
                .build()
                .createSign(unionPayBean.getKey(), SignType.MD5);
        String returnCode = "0";
        try {
            String xmlResult = UnionPayApi.execution(unionPayBean.getServerUrl(), params);
            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
            returnCode = result.get("status");
            String resultCode = result.get("result_code");
            String errMsg = result.get("err_msg");
            String errCode = result.get("err_code");

            logger.info("UnionPayService createPrepayOrder xmlResult: {} ", xmlResult);
            if (!"0".equals(returnCode) || !"0".equals(resultCode)) {
                if (returnCode.equals("10003")) {
                    // 需要会员输入支付密码，等待10秒后查询订单
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> payResult = queryPaidOrder(orderInfo.getStoreId(), "云闪付单号", orderInfo.getOrderSn());
                    if (payResult == null) {
                        throw new BusinessCheckException("云闪付支付失败");
                    }
                } else {
                    throw new BusinessCheckException("云闪付支付出错：" + errCode + errMsg);
                }
            }
        } catch (Exception e) {
            logger.error("UnionPayService createPrepayOrder exception {}", e.getMessage());
            throw new BusinessCheckException("云闪付支付出错，请检查配置项");
        }

        Map<String, String> respData = new HashMap<>();
        respData.put("result", returnCode);

        ResponseObject responseObject = new ResponseObject(200, "云闪付支付接口返回成功", respData);
        logger.info("UnionPayService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    /**
     * 支付回调
     *
     * @param params 请求参数
     * @return
     * */
    @Override
    public Boolean checkCallBack(Map<String, String> params) throws Exception {
        String orderSn = params.get("out_trade_no") != null ? params.get("out_trade_no") : "";
        Integer storeId = 0;
        UserOrderDto orderDto = orderService.getOrderByOrderSn(orderSn);
        if (orderDto != null && orderDto.getStoreInfo() != null) {
            storeId = orderDto.getStoreInfo().getId();
        }
        getApiConfig(storeId);
        return AlipaySignature.rsaCheckV1(params, unionPayBean.getKey(), "UTF-8", "RSA2");
    }

    /**
     * 获取支付配置
     *
     * @param storeId 店铺ID
     * @return
     * */
    public AliPayApiConfig getApiConfig(Integer storeId) throws BusinessCheckException {
        AliPayApiConfig aliPayApiConfig;
        String appId = unionPayBean.getMachId();
        String privateKey = unionPayBean.getMachId();
        String publicKey = unionPayBean.getKey();

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
                .setServiceUrl(unionPayBean.getServerUrl())
                .setSignType("RSA2")
                .build();

        AliPayApiConfigKit.setThreadLocalAppId(appId);
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);

        return aliPayApiConfig;
    }

    /**
     * 查询支付订单
     *
     * @param storeId 店铺ID
     * @param tradeNo 交易单号
     * @param orderSn 订单号
     * @return
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
            logger.info("UnionPayService queryPaidOrder response", e.getMessage());
        }

        return null;
    }

    /**
     * 发起售后退款
     *
     * @param storeId 店铺ID
     * @param orderSn 订单号
     * @param totalAmount 订单总金额
     * @param refundAmount 售后金额
     * @param platform 订单平台
     * @return
     * */
    public Boolean doRefund(Integer storeId, String orderSn, BigDecimal totalAmount, BigDecimal refundAmount, String platform) throws BusinessCheckException {
        try {
            logger.info("UnionPayService.doRefund orderSn = {}, totalFee = {}, refundFee = {}", orderSn, totalAmount, refundAmount);
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
            logger.info("UnionPayService refundResult response Body = {}", refundResponse.getBody());
            if (!code.equals("10000") || !msg.equalsIgnoreCase("Success")) {
                throw new BusinessCheckException("云闪付退款失败，" + subMsg);
            }
        } catch (AlipayApiException e) {
            logger.error("UnionPayService.doRefund error = {}", e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
