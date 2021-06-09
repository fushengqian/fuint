package com.fuint.application.service.confirmlog;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.ConfirmLogDto;


import java.util.List;
import java.util.Map;

/**
 * 会员用户业务接口
 * Created by zach 2019/08/20
 */
public interface ConfirmLogService {

    /**
     * 分页查询会员卡券核销列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 查询会员卡券核销列表
     * */
    List<ConfirmLogDto> queryConfirmLogListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据ID获取用户卡券核销信息
     *
     * @param id
     * @throws BusinessCheckException
     */
    ConfirmLogDto queryConfirmLogById(Integer id) throws BusinessCheckException;

    /**
     * 获取卡券核销次数
     * @param userCouponId
     * @return
     * */
    Integer getConfirmNum(Integer userCouponId) throws BusinessCheckException;
}
