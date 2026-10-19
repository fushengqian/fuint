package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtOrderPay;

import java.math.BigDecimal;

/**
 * 订单支付单业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface OrderPayService {

    /**
     * 创建支付单，生成支付单号（订单号保持不变）
     *
     * @param orderInfo 订单信息
     * @param amount 本次应付金额
     * @return
     * */
    MtOrderPay createPayLog(MtOrder orderInfo, BigDecimal amount) throws BusinessCheckException;

    /**
     * 根据支付单号获取支付单
     *
     * @param paySn 支付单号
     * @return
     * */
    MtOrderPay getPayLogByPaySn(String paySn);

    /**
     * 支付单置为已支付
     *
     * @param paySn 支付单号
     * @return
     * */
    Boolean updatePayLogPaid(String paySn);
}
