package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.BalanceDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BalanceService extends IService<MtBalance> {

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
    Boolean addBalance(MtBalance reqDto) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn) throws BusinessCheckException;
}
