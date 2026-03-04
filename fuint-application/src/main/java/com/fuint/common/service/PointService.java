package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.member.PointDto;
import com.fuint.common.param.PointPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPoint;

/**
 * 积分业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface PointService extends IService<MtPoint> {

    /**
     * 分页查询积分列表
     *
     * @param pointPage
     * @return
     */
    PaginationResponse<PointDto> queryPointListByPagination(PointPage pointPage);

    /**
     * 添加积分
     *
     * @param  reqPointDto
     * @throws BusinessCheckException
     * @return
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
     * @return
     */
    boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException;
}
