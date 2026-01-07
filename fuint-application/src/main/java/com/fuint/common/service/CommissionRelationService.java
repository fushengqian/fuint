package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.param.CommissionRelationPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtCommissionRelation;
import com.fuint.repository.model.MtUser;

/**
 * 分销提成关系业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionRelationService extends IService<MtCommissionRelation> {

    /**
     * 分页查询分佣关系列表
     *
     * @param commissionRelationPage
     * @return
     */
    PaginationResponse<CommissionRelationDto> queryRelationByPagination(CommissionRelationPage commissionRelationPage) throws BusinessCheckException;

    /**
     * 设置分销提成关系
     *
     * @param  userInfo 会员信息
     * @param  shareId 分享者ID
     * @throws BusinessCheckException
     * @retrurn
     */
    void setCommissionRelation(MtUser userInfo, String shareId) throws BusinessCheckException;
}
