package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.CommissionCashDto;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.module.backendApi.request.CommissionCashRequest;
import com.fuint.module.backendApi.request.CommissionSettleConfirmRequest;
import com.fuint.module.backendApi.request.CommissionSettleRequest;
import com.fuint.repository.model.MtCommissionCash;

/**
 * 分销提成记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionCashService extends IService<MtCommissionCash> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<CommissionCashDto> queryCommissionCashByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 计算订单分销提成
     *
     * @param  commissionSettleRequest 结算参数
     * @throws BusinessCheckException
     * @return
     */
    String settleCommission(CommissionSettleRequest commissionSettleRequest) throws BusinessCheckException;

    /**
     * 根据ID获取记录信息
     *
     * @param  id 记录ID
     * @throws BusinessCheckException
     * @return
     */
    CommissionCashDto queryCommissionCashById(Integer id) throws BusinessCheckException;

    /**
     * 更新分销提成记录
     *
     * @param commissionCashRequest 请求参数
     * @throws BusinessCheckException
     * @return
     */
    void updateCommissionCash(CommissionCashRequest commissionCashRequest) throws BusinessCheckException;

    /**
     * 结算确认
     *
     * @param requestParam 确认参数
     * @throws BusinessCheckException
     * @return
     */
    void confirmCommissionCash(CommissionSettleConfirmRequest requestParam) throws BusinessCheckException;

    /**
     * 取消结算
     *
     * @param requestParam 取消参数
     * @throws BusinessCheckException
     * @return
     */
    void cancelCommissionCash(CommissionSettleConfirmRequest requestParam) throws BusinessCheckException;

}
