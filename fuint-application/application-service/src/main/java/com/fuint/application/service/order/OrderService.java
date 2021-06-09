package com.fuint.application.service.order;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.OrderDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 订单业务接口
 * Created by zach 2021/05/05
 */
public interface OrderService {
    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtOrder> getOrderListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 获取用户的订单
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject getUserOrderList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 创建订单
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtOrder createOrder(OrderDto reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     */
    MtOrder getOrderById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID 删除订单信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteOrder(Integer id, String operator) throws BusinessCheckException;

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
}
