package com.fuint.application.service.balance;

import com.fuint.application.dao.entities.MtBalance;
import com.fuint.application.dto.BalanceDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

/**
 * 余额业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface BalanceService {

    /**
     * 分页查询余额列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加余额记录
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    boolean addBalance(MtBalance reqDto) throws BusinessCheckException;
}
