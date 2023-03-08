package com.fuint.common.service.impl;

import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.service.*;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private WeixinService weixinService;

    /**
     * 创建支付订单
     * @return
     * */
    @Override
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip) throws BusinessCheckException {
        logger.info("PaymentService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);
        ResponseObject responseObject = weixinService.createPrepayOrder(userInfo, orderInfo, payAmount, authCode, giveAmount, ip);
        logger.info("PaymentService createPrepayOrder outParams {}", responseObject.toString());
        return responseObject;
    }

    /**
     * 支付回调
     * @return
     * */
    @Override
    @Transactional
    public boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException {
        Boolean result = weixinService.paymentCallback(orderInfo);
        if (!result) {
            logger.error("PaymentService paymentCallback error orderSn {}", orderInfo.getOrderSn());
        }
        logger.info("PaymentService paymentCallback Success orderSn {}", orderInfo.getOrderSn());
        return true;
    }
}
