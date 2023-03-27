package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
    MtCommissionRule addCommissionRule(MtCommissionRule commissionRule) throws BusinessCheckException;

    /**
     * 根据ID获取规则信息
     *
     * @param  id
     * @throws BusinessCheckException
     */
    MtCommissionRule queryCommissionRuleById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除分销提成规则
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCommissionRule(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新数据
     * @param  commissionRule
     * @throws BusinessCheckException
     * */
    MtCommissionRule updateCommissionRule(MtCommissionRule commissionRule) throws BusinessCheckException;

    /**
     * 根据条件搜索分销提成规则
     * */
    List<MtCommissionRule> queryDataByParams(Map<String, Object> params) throws BusinessCheckException;

}
