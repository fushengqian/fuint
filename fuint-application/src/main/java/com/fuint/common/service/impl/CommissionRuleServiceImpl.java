package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCommissionRuleMapper;
import com.fuint.common.service.CommissionRuleService;
import com.fuint.common.enums.StatusEnum;

import com.fuint.repository.model.MtCommissionRule;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 分销提成服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CommissionRuleServiceImpl extends ServiceImpl<MtCommissionRuleMapper, MtCommissionRule> implements CommissionRuleService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionRuleServiceImpl.class);

    @Resource
    private MtCommissionRuleMapper mtCommissionRuleMapper;

    /**
     * 分页查询规则列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCommissionRule> queryDataByPagination(PaginationRequest paginationRequest) {
        Page<MtCommissionRule> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionRule> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionRule::getStatus, StatusEnum.DISABLE.getKey());

        String title = paginationRequest.getSearchParams().get("title") == null ? "" : paginationRequest.getSearchParams().get("title").toString();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtCommissionRule::getName, title);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getStatus, status);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtCommissionRule::getId);
        List<MtCommissionRule> dataList = mtCommissionRuleMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtCommissionRule> paginationResponse = new PaginationResponse(pageImpl, MtCommissionRule.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加分销提成规则
     *
     * @param commissionRule
     */
    @Override
    @OperationServiceLog(description = "新增分销提成规则")
    public MtCommissionRule addCommissionRule(MtCommissionRule commissionRule) {
        MtCommissionRule mtCommissionRule = new MtCommissionRule();
        Integer id = mtCommissionRuleMapper.insert(mtCommissionRule);
        if (id > 0) {
            return mtCommissionRule;
        } else {
            logger.error("新增分销提成规则失败...");
            return null;
        }
    }

    /**
     * 根据ID获取规则信息
     *
     * @param id
     */
    @Override
    public MtCommissionRule queryCommissionRuleById(Integer id) {
        return mtCommissionRuleMapper.selectById(id);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @param operator 操作人
     */
    @Override
    @OperationServiceLog(description = "删除分销提成规则")
    public void deleteCommissionRule(Integer id, String operator) {
        MtCommissionRule mtCommissionRule = queryCommissionRuleById(id);
        if (mtCommissionRule == null) {
            logger.error("删除分销提成规则失败...");
            return;
        }
        mtCommissionRule.setStatus(StatusEnum.DISABLE.getKey());
        mtCommissionRule.setUpdateTime(new Date());
        mtCommissionRuleMapper.updateById(mtCommissionRule);
    }

    /**
     * 更新分销提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新分销提成规则")
    public MtCommissionRule updateCommissionRule(MtCommissionRule commissionRule) throws BusinessCheckException {
        MtCommissionRule mtCommissionRule = queryCommissionRuleById(commissionRule.getId());
        if (mtCommissionRule == null) {
            logger.error("更新分销提成规则失败...");
            throw new BusinessCheckException("该数据状态异常");
        }
        mtCommissionRule.setId(commissionRule.getId());
        if (commissionRule.getStoreId() != null) {
            mtCommissionRule.setStoreId(commissionRule.getStoreId());
        }
        if (commissionRule.getDescription() != null) {
            mtCommissionRule.setDescription(commissionRule.getDescription());
        }
        if (commissionRule.getOperator() != null) {
            mtCommissionRule.setOperator(commissionRule.getOperator());
        }
        if (commissionRule.getStatus() != null) {
            mtCommissionRule.setStatus(commissionRule.getStatus());
        }
        mtCommissionRule.setUpdateTime(new Date());
        mtCommissionRuleMapper.updateById(mtCommissionRule);
        return mtCommissionRule;
    }

    @Override
    public List<MtCommissionRule> queryDataByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();

        LambdaQueryWrapper<MtCommissionRule> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtCommissionRule::getName, name);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getStatus, status);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                              .eq(MtCommissionRule::getStoreId, 0)
                              .or()
                              .eq(MtCommissionRule::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtCommissionRule::getId);
        List<MtCommissionRule> dataList = mtCommissionRuleMapper.selectList(lambdaQueryWrapper);

        return dataList;
    }
}
