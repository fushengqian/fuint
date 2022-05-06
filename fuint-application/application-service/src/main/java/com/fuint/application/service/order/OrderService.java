package com.fuint.application.service.order;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtCart;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.exception.BusinessCheckException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface OrderService {

    /**
     * 获取用户的订单
     * @param paramMap
     * @throws BusinessCheckException
     * */
    ResponseObject getUserOrderList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 创建订单
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtOrder saveOrder(OrderDto reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取订单
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    UserOrderDto getOrderById(Integer id) throws BusinessCheckException;

    /**
     * 根据订单号获取订单
     *
     * @param orderSn
     * @throws BusinessCheckException
     */
    UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException;

    /**
     * 更新订单
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtOrder updateOrder(OrderDto reqDto) throws BusinessCheckException;

    /**
     * 根据条件搜索订单
     * */
    List<MtOrder> getOrderListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 获取订单总数
     * */
    BigDecimal getOrderCount(Integer storeId) throws BusinessCheckException;

    /**
     * 获取订单数量
     * */
    BigDecimal getOrderCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 计算购物车
     * */
    Map<String, Object> calculateCartGoods(Integer userId, List<MtCart> cartList, Integer couponId, boolean isUsePoint) throws BusinessCheckException;

    /**
     * 获取支付金额
     * */
    BigDecimal getPayMoney(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 获取支付人数
     * */
    Integer getPayUserCount(Integer storeId) throws BusinessCheckException;

    /**
     * 获取支付金额
     * */
    BigDecimal getPayMoney(Integer storeId) throws BusinessCheckException;
}
