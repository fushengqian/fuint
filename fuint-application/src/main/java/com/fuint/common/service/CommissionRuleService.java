package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.CommissionRuleDto;
import com.fuint.common.param.CommissionRuleParam;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtCommissionRule;

import java.util.List;
import java.util.Map;

/**
 * 分销提成规则业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionRuleService extends IService<MtCommissionRule> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCommissionRule> queryDataByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加分销提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     */
    MtCommissionRule addCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

    /**
     * 根据ID获取规则信息
     *
     * @param  id
     * @throws BusinessCheckException
     */
    CommissionRuleDto queryCommissionRuleById(Integer id) throws BusinessCheckException;

    /**
     * 更新数据
     * @param  commissionRule
     * @throws BusinessCheckException
     * */
    MtCommissionRule updateCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

}
