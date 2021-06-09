package com.fuint.application.service.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.application.dao.repositories.MtPointRepository;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 积分管理业务实现类
 * Created by zach 2021/3/15
 */
@Service
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    @Autowired
    private MtPointRepository pointRepository;

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtPoint> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtPoint> paginationResponse = pointRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加积分记录信息
     *
     * @param mtPoint
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加积分记录")
    public MtPoint addPoint(MtPoint mtPoint) throws BusinessCheckException {
        return pointRepository.save(mtPoint);
    }
}
