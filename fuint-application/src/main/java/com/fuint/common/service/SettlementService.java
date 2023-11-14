package com.fuint.common.service;

import com.fuint.common.dto.SettlementDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.SettlementRequest;
import com.fuint.repository.model.MtSettlement;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SettlementService {

    /**
     * 分页查询结算列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSettlement> querySettlementListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 提交结算
     *
     * @param  requestParam
     * @throws BusinessCheckException
     */
    Boolean submitSettlement(SettlementRequest requestParam) throws BusinessCheckException;

    /**
     * 结算确认
     *
     * @param  settlementId
     * @param  operator
     * @throws BusinessCheckException
     */
    Boolean doConfirm(Integer settlementId, String operator) throws BusinessCheckException;

    /**
     * 获取结算详情
     *
     * @param settlementId
     * @param page
     * @param pageSize
     *
     * @return
     * */
    SettlementDto getSettlementInfo(Integer settlementId, Integer page, Integer pageSize) throws BusinessCheckException;
}