package com.fuint.common.service;

import com.fuint.common.dto.UserOrderDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;

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
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip) throws BusinessCheckException;

    /**
     * 支付回调
     * */
    boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;

}