package com.fuint.common.service;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.ExpressTraceResultDto;
import com.fuint.framework.exception.BusinessCheckException;

/**
 * 物流查询服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ExpressService {

    /**
     * 根据订单ID查询物流轨迹
     *
     * @param orderId 订单ID
     * @param userInfo 当前登录会员
     * @return 物流轨迹
     */
    ExpressTraceResultDto queryExpressTrace(Integer orderId, UserInfo userInfo) throws BusinessCheckException;

    /**
     * 根据订单号查询物流轨迹
     *
     * @param orderSn 订单号
     * @param userInfo 当前登录会员
     * @return 物流轨迹
     */
    ExpressTraceResultDto queryExpressTraceByOrderSn(String orderSn, UserInfo userInfo) throws BusinessCheckException;
}
