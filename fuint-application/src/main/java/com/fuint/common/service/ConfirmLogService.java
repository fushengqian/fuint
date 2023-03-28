package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ConfirmLogDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtConfirmLog;

import java.util.Date;
import java.util.List;

/**
 * 核销记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ConfirmLogService extends IService<MtConfirmLog> {

    /**
     * 分页查询会员卡券核销列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 获取卡券核销次数
     * @param userCouponId
     * @return
     * */
    Long getConfirmNum(Integer userCouponId) throws BusinessCheckException;

    /**
     * 获取卡券核销列表
     * @param userCouponId
     * @return
     * */
    List<MtConfirmLog> getConfirmList(Integer userCouponId) throws BusinessCheckException;

    /**
     * 获取核销总数
     * */
    Long getConfirmCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;
}
