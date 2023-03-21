package com.fuint.common.service;

import com.fuint.common.dto.UserOrderDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface PaymentService {

    /**
     * 创建支付单
     * */
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException;

    /**
     * 支付回调
     * */
    Boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;

    /**
     * 订单支付
     * */
    Map<String, Object> doPay(HttpServletRequest request) throws BusinessCheckException;

}