package com.fuint.application.service.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

/**
 * 积分业务接口
 * Created by zach 2021/3/15
 */
public interface PointService {

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtPoint> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加积分
     *
     * @param reqPointDto
     * @throws BusinessCheckException
     */
    MtPoint addPoint(MtPoint reqPointDto) throws BusinessCheckException;
}
