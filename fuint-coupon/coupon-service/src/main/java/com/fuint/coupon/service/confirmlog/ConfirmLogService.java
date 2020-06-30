package com.fuint.coupon.service.confirmlog;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dto.ConfirmLogDto;


import java.util.List;
import java.util.Map;

/**
 * 会员用户业务接口
 * Created by zach 20190820
 */
public interface ConfirmLogService {

    /**
     * 分页查询会员优惠券核销列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;


    /**
     * 查询会员优惠券核销列表
     * */
    public List<ConfirmLogDto> queryConfirmLogListByParams(Map<String, Object> params) throws BusinessCheckException;


    /**
     * 根据ID获取用户优惠券信息
     *
     * @param id 用户优惠券id
     * @throws BusinessCheckException
     */
    ConfirmLogDto queryConfirmLogById(Integer id) throws BusinessCheckException;

}
