package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.common.param.CommissionRuleItemParam;
import com.fuint.common.param.CommissionRuleParam;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCommissionRuleItemMapper;
import com.fuint.repository.mapper.MtCommissionRuleMapper;
import com.fuint.common.service.CommissionRuleService;
import com.fuint.common.enums.StatusEnum;

import com.fuint.repository.model.MtCommissionRule;
import com.fuint.repository.model.MtCommissionRuleItem;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 分销提成服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class CommissionRuleServiceImpl extends ServiceImpl<MtCommissionRuleMapper, MtCommissionRule> implements CommissionRuleService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionRuleServiceImpl.class);

    private MtCommissionRuleMapper mtCommissionRuleMapper;

    private MtCommissionRuleItemMapper mtCommissionRuleItemMapper;

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

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtCommissionRule::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getStatus, status);
        }
        String target = paginationRequest.getSearchParams().get("target") == null ? "" : paginationRequest.getSearchParams().get("target").toString();
        if (StringUtils.isNotBlank(target)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getTarget, target);
        }
        String type = paginationRequest.getSearchParams().get("type") == null ? "" : paginationRequest.getSearchParams().get("type").toString();
        if (StringUtils.isNotBlank(type)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getType, type);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtCommissionRule::getMerchantId, merchantId);
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
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增分销提成规则")
    public MtCommissionRule addCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException {
        MtCommissionRule mtCommissionRule = new MtCommissionRule();
        BeanUtils.copyProperties(commissionRule, mtCommissionRule);
        mtCommissionRule.setStatus(StatusEnum.ENABLED.getKey());
        Date date = new Date();
        mtCommissionRule.setCreateTime(date);
        mtCommissionRule.setUpdateTime(date);
        mtCommissionRule.setMerchantId(mtCommissionRule.getMerchantId()== null ? 0 : mtCommissionRule.getMerchantId());
        boolean result = save(mtCommissionRule);
        if (result) {
            if (commissionRule.getDetailList() != null && commissionRule.getDetailList().size() > 0) {
                String storeIds = StringUtil.join(commissionRule.getStoreIdList().toArray(), ",");
                for (CommissionRuleItemParam itemParam : commissionRule.getDetailList()) {
                     MtCommissionRuleItem mtCommissionRuleItem = new MtCommissionRuleItem();
                     mtCommissionRuleItem.setRuleId(mtCommissionRule.getId());
                     mtCommissionRuleItem.setType(mtCommissionRule.getType());
                     mtCommissionRuleItem.setTarget(mtCommissionRule.getTarget());
                     mtCommissionRuleItem.setMerchantId(mtCommissionRule.getMerchantId());
                     mtCommissionRuleItem.setStoreId(mtCommissionRule.getStoreId());
                     mtCommissionRuleItem.setStoreIds(storeIds);
                     mtCommissionRuleItem.setCreateTime(date);
                     mtCommissionRuleItem.setUpdateTime(date);
                     mtCommissionRuleItem.setOperator(commissionRule.getOperator());
                     mtCommissionRuleItem.setStatus(mtCommissionRule.getStatus());
                     mtCommissionRuleItem.setMethod(itemParam.getMethod());
                     mtCommissionRuleItem.setTarget(commissionRule.getTarget());
                     mtCommissionRuleItem.setTargetId(itemParam.getGoodsId());
                     mtCommissionRuleItem.setMember(itemParam.getMemberVal());
                     mtCommissionRuleItem.setGuest(itemParam.getVisitorVal());
                     mtCommissionRuleItemMapper.insert(mtCommissionRuleItem);
                }
            }
        } else {
            logger.error("新增分销提成规则失败...");
            throw new BusinessCheckException("新增分销方案规则失败");
        }
        return mtCommissionRule;
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
     * 更新分销提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新分销提成规则")
    public MtCommissionRule updateCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException {
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
            if (commissionRule.getStatus().equals(StatusEnum.DISABLE.getKey())) {
                mtCommissionRuleItemMapper.deleteByRuleId(commissionRule.getId(), new Date());
            }
        }
        mtCommissionRule.setUpdateTime(new Date());
        mtCommissionRuleMapper.updateById(mtCommissionRule);
        return mtCommissionRule;
    }

    @Override
    public List<MtCommissionRule> queryCommissionRuleByParams(Map<String, Object> params) {
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
