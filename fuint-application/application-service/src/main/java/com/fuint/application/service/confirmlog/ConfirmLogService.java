package com.fuint.application.service.confirmlog;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.ConfirmLogDto;
import java.util.Date;

/**
 * 核销记录业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
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
     * 获取卡券核销次数
     * @param userCouponId
     * @return
     * */
    Long getConfirmNum(Integer userCouponId) throws BusinessCheckException;

    /**
     * 获取核销总数
     * */
    Long getConfirmCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;
}
