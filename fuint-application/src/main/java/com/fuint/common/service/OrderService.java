package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.param.RechargeParam;
import com.fuint.common.param.SettlementParam;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtCart;
import com.fuint.repository.model.MtOrder;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface OrderService extends IService<MtOrder> {

    /**
     * 获取用户的订单
     *
     * @param  orderListParam
     * @throws BusinessCheckException
     * @return
     * */
    PaginationResponse getUserOrderList(OrderListParam orderListParam) throws BusinessCheckException;

    /**
     * 创建订单
     *
     * @param  orderDto
     * @throws BusinessCheckException
     * @return
     */
    MtOrder saveOrder(OrderDto orderDto) throws BusinessCheckException;

    /**
     * 订单提交结算
     *
     * @param request 请求参数
     * @param settlementParam 结算参数
     * @throws BusinessCheckException
     * @return
     * */
    Map<String, Object> doSettle(HttpServletRequest request, SettlementParam settlementParam) throws BusinessCheckException;

    /**
     * 获取订单详情
     *
     * @param  id 订单ID
     * @throws BusinessCheckException
     * @return
     */
    MtOrder getOrderInfo(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取订单
     *
     * @param  id 订单ID
     * @throws BusinessCheckException
     * @return
     */
    UserOrderDto getOrderById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取订单
     *
     * @param  id
     * @throws BusinessCheckException
     * @return
     */
    UserOrderDto getMyOrderById(Integer id) throws BusinessCheckException;

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param remark 取消备注
     * @throws BusinessCheckException
     * @return
     * */
    MtOrder cancelOrder(Integer orderId, String remark) throws BusinessCheckException;

    /**
     * 根据订单ID删除
     *
     * @param  orderId 订单ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteOrder(Integer orderId, String operator) throws BusinessCheckException;

    /**
     * 根据订单号获取订单
     *
     * @param  orderSn
     * @throws BusinessCheckException
     * @return
     */
    UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException;

    /**
     * 根据订单号获取订单
     *
     * @param orderSn 订单号
     * @return
     * */
    MtOrder getOrderInfoByOrderSn(String orderSn);

    /**
     * 更新订单
     *
     * @param  reqDto
     * @throws BusinessCheckException
     * @return
     * */
    MtOrder updateOrder(OrderDto reqDto) throws BusinessCheckException;

    /**
     * 更新订单
     *
     * @param  mtOrder
     * @throws BusinessCheckException
     * @return
     * */
    MtOrder updateOrder(MtOrder mtOrder) throws BusinessCheckException;

    /**
     * 把订单置为已支付
     *
     * @param orderId
     * @param payAmount
     * @throws BusinessCheckException
     * @return
     * */
    Boolean setOrderPayed(Integer orderId, BigDecimal payAmount) throws BusinessCheckException;

    /**
     * 根据条件搜索订单
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtOrder> getOrderListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 获取订单总数
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @throws BusinessCheckException
     * @return
     * */
    BigDecimal getOrderCount(Integer merchantId, Integer storeId) throws BusinessCheckException;

    /**
     * 获取订单数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @throws BusinessCheckException
     * @return
     * */
    BigDecimal getOrderCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 计算购物车
     *
     * @param merchantId 商户ID
     * @param userId 会员ID
     * @param cartList 购物车列表
     * @param couponId 使用的卡券ID
     * @param isUsePoint 是否使用积分抵扣
     * @param platform 平台 h5
     * @param orderMode 订单模式，自取或配送
     * @throws BusinessCheckException
     * @return
     * */
    Map<String, Object> calculateCartGoods(Integer merchantId, Integer userId, List<MtCart> cartList, Integer couponId, boolean isUsePoint, String platform, String orderMode) throws BusinessCheckException;

    /**
     * 获取支付金额
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @throws BusinessCheckException
     * @return
     * */
    BigDecimal getPayMoney(Integer merchantId, Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 获取支付人数
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @throws BusinessCheckException
     * @return
     * */
    Integer getPayUserCount(Integer merchantId, Integer storeId) throws BusinessCheckException;

    /**
     * 获取支付金额
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @throws BusinessCheckException
     * @return
     * */
    BigDecimal getPayMoney(Integer merchantId, Integer storeId) throws BusinessCheckException;

    /**
     * 获取会员支付金额
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     * @return
     * */
    BigDecimal getUserPayMoney(Integer userId) throws BusinessCheckException;

    /**
     * 获取会员订单数
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     * @return
     * */
    Integer getUserPayOrderCount(Integer userId) throws BusinessCheckException;

    /**
     * 获取等待分佣的订单列表
     *
     * @param dateTime 时间
     * @throws BusinessCheckException
     * @return
     * */
    List<MtOrder> getTobeCommissionOrderList(String dateTime) throws BusinessCheckException;

    /**
     * 提交充值订单
     *
     * @param rechargeParam 充值参数
     * @return
     * */
    MtOrder doRecharge(HttpServletRequest request, RechargeParam rechargeParam) throws BusinessCheckException;
}
