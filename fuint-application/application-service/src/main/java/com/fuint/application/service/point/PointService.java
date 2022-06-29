package com.fuint.application.service.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.application.dto.PointDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

/**
 * 积分业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface PointService {

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<PointDto> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加积分
     *
     * @param reqPointDto
     * @throws BusinessCheckException
     */
    void addPoint(MtPoint reqPointDto) throws BusinessCheckException;

    /**
     * 转赠积分
     *
     * @param userId
     * @param mobile
     * @param amount
     * @param remark
     * @throws BusinessCheckException
     */
    boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException;
}
