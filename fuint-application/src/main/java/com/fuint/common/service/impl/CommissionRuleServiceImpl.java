package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.common.dto.CommissionRuleDto;
import com.fuint.common.dto.CommissionRuleItemDto;
import com.fuint.common.param.CommissionRuleItemParam;
import com.fuint.common.param.CommissionRuleParam;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.SettingService;
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
import com.fuint.repository.model.MtGoods;
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
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

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
        String storeIds = StringUtil.join(commissionRule.getStoreIdList().toArray(), ",");
        mtCommissionRule.setStoreIds(storeIds);
        boolean result = save(mtCommissionRule);
        if (result) {
            if (commissionRule.getDetailList() != null && commissionRule.getDetailList().size() > 0) {
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
     * @param id 规则ID
     * @return
     */
    @Override
    public CommissionRuleDto queryCommissionRuleById(Integer id) throws BusinessCheckException {
        MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(id);
        if (mtCommissionRule == null) {
            return null;
        }
        CommissionRuleDto commissionRuleDto = new CommissionRuleDto();
        BeanUtils.copyProperties(mtCommissionRule, commissionRuleDto);

        Map<String, Object> param = new HashMap();
        param.put("RULE_ID", id);
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtCommissionRuleItem> mtCommissionRuleItems = mtCommissionRuleItemMapper.selectByMap(param);
        List<CommissionRuleItemDto> detailList = new ArrayList<>();
        String basePath = settingService.getUploadBasePath();
        if (mtCommissionRuleItems != null && mtCommissionRuleItems.size() > 0) {
            for (MtCommissionRuleItem item : mtCommissionRuleItems) {
                 CommissionRuleItemDto commissionRuleItemDto = new CommissionRuleItemDto();
                 commissionRuleItemDto.setGoodsId(item.getTargetId());
                 MtGoods mtGoods = goodsService.queryGoodsById(item.getTargetId());
                 if (mtGoods != null) {
                     commissionRuleItemDto.setGoodsName(mtGoods.getName());
                     commissionRuleItemDto.setLogo(basePath + mtGoods.getLogo());
                     commissionRuleItemDto.setPrice(mtGoods.getPrice());
                 }
                 commissionRuleItemDto.setType(item.getType());
                 commissionRuleItemDto.setMemberVal(item.getMember());
                 commissionRuleItemDto.setMethod(item.getMethod());
                 commissionRuleItemDto.setVisitorVal(item.getGuest());
                 detailList.add(commissionRuleItemDto);
            }
        }
        commissionRuleDto.setDetailList(detailList);

        List<Integer> storeIds = new ArrayList<>();
        if (StringUtil.isNotEmpty(mtCommissionRule.getStoreIds())) {
            List<String> storeIdList = Arrays.asList(mtCommissionRule.getStoreIds().split(","));
            if (storeIdList != null && storeIdList.size() > 0) {
                for (String storeId : storeIdList) {
                     storeIds.add(Integer.parseInt(storeId));
                }
            }
        }
        commissionRuleDto.setStoreIdList(storeIds);

        return commissionRuleDto;
    }

    /**
     * 更新分销提成规则
     *
     * @param  commissionRule 规则参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新分销提成规则")
    public MtCommissionRule updateCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException {
        MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(commissionRule.getId());
        if (mtCommissionRule == null) {
            logger.error("更新分销提成规则失败...");
            throw new BusinessCheckException("该数据状态异常");
        }
        mtCommissionRule.setId(commissionRule.getId());
        if (commissionRule.getName() != null) {
            mtCommissionRule.setName(commissionRule.getName());
        }
        if (commissionRule.getTarget() != null) {
            mtCommissionRule.setTarget(commissionRule.getTarget());
        }
        if (commissionRule.getType() != null) {
            mtCommissionRule.setType(commissionRule.getType());
        }
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
        String storeIds = "";
        if (commissionRule.getStoreIdList() != null && commissionRule.getStoreIdList().size() > 0) {
            storeIds = StringUtil.join(commissionRule.getStoreIdList().toArray(), ",");
            mtCommissionRule.setStoreIds(storeIds);
        }

        // 更新或插入
        Date date = new Date();
        List<Integer> itemIds = new ArrayList<>();
        if (commissionRule.getDetailList() != null && commissionRule.getDetailList().size() > 0) {
            for (CommissionRuleItemParam itemParam : commissionRule.getDetailList()) {
                MtCommissionRuleItem mtCommissionRuleItem = new MtCommissionRuleItem();
                mtCommissionRuleItem.setRuleId(mtCommissionRule.getId());
                mtCommissionRuleItem.setType(mtCommissionRule.getType());
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
                // 判断是否已经存在，存在则更新
                if (itemParam.getGoodsId() != null && itemParam.getGoodsId() > 0) {
                    Map<String, Object> param = new HashMap();
                    param.put("RULE_ID", commissionRule.getId());
                    param.put("TARGET_ID", itemParam.getGoodsId());
                    param.put("STATUS", StatusEnum.ENABLED.getKey());
                    List<MtCommissionRuleItem> items = mtCommissionRuleItemMapper.selectByMap(param);
                    if (items != null && items.size() > 0) {
                        mtCommissionRuleItem.setId(items.get(0).getId());
                        itemIds.add(items.get(0).getId());
                    }
                }
                if (mtCommissionRuleItem.getId() != null && mtCommissionRuleItem.getId() > 0) {
                    mtCommissionRuleItemMapper.updateById(mtCommissionRuleItem);
                } else {
                    mtCommissionRuleItemMapper.insert(mtCommissionRuleItem);
                    itemIds.add(mtCommissionRuleItem.getId());
                }
            }
        }

        // 删除
        Map<String, Object> params = new HashMap();
        params.put("RULE_ID", commissionRule.getId());
        params.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtCommissionRuleItem> mtCommissionRuleItems = mtCommissionRuleItemMapper.selectByMap(params);
        for (MtCommissionRuleItem item : mtCommissionRuleItems) {
             if (!itemIds.contains(item.getId())) {
                 item.setStatus(StatusEnum.DISABLE.getKey());
                 mtCommissionRuleItemMapper.updateById(item);
             }
        }

        mtCommissionRule.setUpdateTime(new Date());
        mtCommissionRuleMapper.updateById(mtCommissionRule);
        return mtCommissionRule;
    }
}
