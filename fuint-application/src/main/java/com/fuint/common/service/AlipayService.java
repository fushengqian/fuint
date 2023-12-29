package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface AlipayService {

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
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException;

    /**
     * 支付回调
     *
     * @param params 请求参数
     * @return
     * */
    Boolean checkCallBack(Map<String, String> params) throws Exception;

    /**
     * 查询支付订单
     *
     * @param storeId 店铺ID
     * @param tradeNo 交易单号
     * @param orderSn 订单号
     * @return
     * */
    Map<String, String> queryPaidOrder(Integer storeId, String tradeNo, String orderSn) throws BusinessCheckException;

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
    Boolean doRefund(Integer storeId, String orderSn, BigDecimal totalAmount, BigDecimal refundAmount, String platform) throws BusinessCheckException;

}