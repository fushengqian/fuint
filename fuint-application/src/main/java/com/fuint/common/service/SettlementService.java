package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
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
     * @param  mtSettlement
     * @throws BusinessCheckException
     */
    Boolean submitSettlement(MtSettlement mtSettlement) throws BusinessCheckException;

    /**
     * 获取结算详情
     *
     * @param settlementId
     * @return
     * */
    MtSettlement getSettlementInfo(Integer settlementId) throws BusinessCheckException;
}