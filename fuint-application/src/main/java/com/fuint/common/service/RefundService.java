package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.order.RefundDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.RefundPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.RefundListRequest;
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
     * @param refundPage
     * @return
     */
    PaginationResponse<RefundDto> getRefundListByPagination(RefundPage refundPage);

    /**
     * 获取用户售后订单
     *
     * @param param 查询参数
     * @return
     * */
    ResponseObject getUserRefundList(RefundListRequest param);

    /**
     * 创建售后订单
     *
     * @param refundDto
     * @return
     */
    MtRefund createRefund(RefundDto refundDto);

    /**
     * 根据ID获取售后订单信息
     *
     * @param id ID
     * @return
     */
    RefundDto getRefundById(Integer id);

    /**
     * 根据订单ID获取售后订单信息
     *
     * @param  orderId
     * @return
     */
    MtRefund getRefundByOrderId(Integer orderId);

    /**
     * 更新售后订单
     * @param  reqDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * */
    MtRefund updateRefund(RefundDto reqDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 同意售后订单
     * @param  reqDto
     * @throws BusinessCheckException
     * */
    MtRefund agreeRefund(RefundDto reqDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 发起退款
     *
     * @param orderId 订单号
     * @param refundAmount 退款金额
     * @param remark 备注
     * @param accountInfo 操作人信息
     * throws BusinessCheckException;
     * */
    Boolean doRefund(Integer orderId, String refundAmount, String remark, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取售后订单总数
     *
     * @param beginTime
     * @param endTime
     * @return
     * */
    Long getRefundCount(Date beginTime, Date endTime);
}
