package com.fuint.common.service.impl;

import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePayResponse;
import com.fuint.common.bean.AliPayBean;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
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

        getApiConfig();
        String notifyUrl = aliPayBean.getDomain();
        AlipayTradePayModel model = new AlipayTradePayModel();
        model.setAuthCode(authCode);
        model.setSubject(goodsInfo);
        model.setTotalAmount(payAmount.toString());
        model.setOutTradeNo(orderInfo.getOrderSn());
        model.setStoreId(orderInfo.getStoreId().toString());
        model.setScene("bar_code");

        String code = "";
        try {
            AlipayTradePayResponse response = AliPayApi.tradePayToResponse(model, notifyUrl);
            code = response.getCode();
            String msg = response.getMsg();
            if (!msg.equals("SUCCESS")) {
                throw new BusinessCheckException("支付宝支付出错，请检查配置项.");
            }
        } catch (Exception e) {
            throw new BusinessCheckException("支付宝支付出错，请检查配置项.");
        }

        Map<String, String> respData = new HashMap<>();
        respData.put("result", code);

        ResponseObject responseObject = new ResponseObject(200, "支付宝支付接口返回成功", respData);
        logger.info("AlipayService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    @Override
    public Boolean checkCallBack( Map<String, String> params) throws Exception {
       getApiConfig();
       return AlipaySignature.rsaCheckV1(params, aliPayBean.getPublicKey(), "UTF-8", "RSA2");
    }

    /**
     * 获取支付配置
     * */
    public AliPayApiConfig getApiConfig() {
        AliPayApiConfig aliPayApiConfig;

        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(aliPayBean.getAppId());
        } catch (Exception e) {
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(aliPayBean.getAppId())
                    .setAliPayPublicKey(aliPayBean.getPublicKey())
                    .setCharset("UTF-8")
                    .setPrivateKey(aliPayBean.getPrivateKey())
                    .setServiceUrl(aliPayBean.getServerUrl())
                    .setSignType("RSA2")
                    .build();
        }

        AliPayApiConfigKit.setThreadLocalAppId(aliPayBean.getAppId());
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);

        return aliPayApiConfig;
    }
}
