package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.RefundDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtRefund;

import java.util.Date;
import java.util.Map;

/**
 * 售后业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface RefundService extends IService<MtRefund> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtRefund> getRefundListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 获取用户的售后订单
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject getUserRefundList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 创建售后订单
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtRefund createRefund(RefundDto reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取售后订单信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    RefundDto getRefundById(Integer id) throws BusinessCheckException;

    /**
     * 根据订单ID获取售后订单信息
     *
     * @param  orderId
     * @throws BusinessCheckException
     */
    MtRefund getRefundByOrderId(Integer orderId) throws BusinessCheckException;

    /**
     * 更新售后订单
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtRefund updateRefund(RefundDto reqDto) throws BusinessCheckException;

    /**
     * 同意售后订单
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtRefund agreeRefund(RefundDto reqDto) throws BusinessCheckException;

    /**
     * 获取售后订单总数
     * */
    Long getRefundCount(Date beginTime, Date endTime) throws BusinessCheckException;
}
