package com.fuint.common.service;

import com.fuint.common.dto.UserOrderDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;

/**
 * 支付宝相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface AlipayService {

    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException;

    Boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;

    String queryPaidOrder(Integer storeId, String transactionId, String orderSn);

}