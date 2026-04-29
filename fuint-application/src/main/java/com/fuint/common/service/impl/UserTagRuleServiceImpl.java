package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.TagRuleTimeRangeEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.common.service.UserTagRuleService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.mapper.MtUserTagRuleMapper;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserTagRule;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会员标签规则服务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserTagRuleServiceImpl extends ServiceImpl<MtUserTagRuleMapper, MtUserTagRule> implements UserTagRuleService {

    private MtUserTagRuleMapper mtUserTagRuleMapper;

    private MtOrderMapper mtOrderMapper;

    private UserTagRelationService userTagRelationService;

    private MemberService memberService;

    @Override
    public List<MtUserTagRule> getMerchantRuleList(Integer merchantId, String status) {
        return mtUserTagRuleMapper.getMerchantRuleList(merchantId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtUserTagRule addRule(MtUserTagRule rule, Integer merchantId) throws BusinessCheckException {
        // 平台账号没有新增权限
        if (merchantId == null || merchantId <= 0) {
            throw new BusinessCheckException("抱歉，您没有新增权限");
        }

        // 校验标签是否存在
        if (rule.getTagId() == null || rule.getTagId() <= 0) {
            throw new BusinessCheckException("请选择关联标签");
        }

        rule.setStatus(StatusEnum.ENABLED.getKey());
        rule.setCreateTime(new Date());
        rule.setUpdateTime(new Date());

        mtUserTagRuleMapper.insert(rule);
        return rule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtUserTagRule updateRule(MtUserTagRule rule, Integer merchantId) throws BusinessCheckException {
        // 平台账号没有编辑权限
        if (merchantId == null || merchantId <= 0) {
            throw new BusinessCheckException("抱歉，您没有编辑权限");
        }

        MtUserTagRule existRule = mtUserTagRuleMapper.selectById(rule.getId());
        if (existRule == null) {
            throw new BusinessCheckException("规则不存在");
        }

        // 校验商户权限
        if (!merchantId.equals(existRule.getMerchantId())) {
            throw new BusinessCheckException("抱歉，您没有编辑权限");
        }

        existRule.setRuleName(rule.getRuleName());
        existRule.setRuleType(rule.getRuleType());
        existRule.setTimeRange(rule.getTimeRange());
        existRule.setOperatorType(rule.getOperatorType());
        existRule.setThresholdValue(rule.getThresholdValue());
        existRule.setThresholdMax(rule.getThresholdMax());
        existRule.setDescription(rule.getDescription());
        existRule.setIsAuto(rule.getIsAuto());
        existRule.setPriority(rule.getPriority());
        existRule.setOperator(rule.getOperator());
        existRule.setUpdateTime(new Date());

        mtUserTagRuleMapper.updateById(existRule);
        return existRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtUserTagRule rule = mtUserTagRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessCheckException("规则不存在");
        }

        Integer merchantId = accountInfo.getMerchantId();
        String operator = accountInfo.getAccountName();

        // 校验商户权限
        if (merchantId != null && merchantId > 0 && !merchantId.equals(rule.getMerchantId())) {
            throw new BusinessCheckException("抱歉，您没有删除权限");
        }

        rule.setStatus(StatusEnum.DISABLE.getKey());
        rule.setOperator(operator);
        rule.setUpdateTime(new Date());
        mtUserTagRuleMapper.updateById(rule);
    }

    @Override
    public void executeRulesForUser(MtUser user, MtOrder order) {
        if (user == null || user.getMerchantId() == null) {
            return;
        }

        List<MtUserTagRule> rules = mtUserTagRuleMapper.getAutoRuleList(user.getMerchantId());
        if (rules.isEmpty()) {
            return;
        }

        // 获取会员已有标签
        List<Integer> existTagIds = userTagRelationService.getTagIdsByUserId(user.getId());
        List<Integer> newTagIds = new ArrayList<>(existTagIds);

        for (MtUserTagRule rule : rules) {
            try {
                boolean isMatch = checkUserMatchRule(user, rule);
                if (isMatch && !newTagIds.contains(rule.getTagId())) {
                    newTagIds.add(rule.getTagId());
                    log.info("会员[{}]符合规则[{}]，添加标签[{}]", user.getId(), rule.getRuleName(), rule.getTagId());
                }
            } catch (Exception e) {
                log.error("执行规则[{}]异常: {}", rule.getId(), e.getMessage());
            }
        }

        // 更新会员标签
        if (newTagIds.size() != existTagIds.size()) {
            userTagRelationService.setUserTags(user.getId(), newTagIds, "SYSTEM");
        }
    }

    @Override
    public void batchExecuteRules(Integer merchantId) {
        List<MtUserTagRule> rules = mtUserTagRuleMapper.getAutoRuleList(merchantId);
        if (rules.isEmpty()) {
            return;
        }

        // 获取所有会员
        List<Integer> userIds = memberService.getUserIdList(merchantId, null);

        for (Integer userId : userIds) {
            MtUser user = memberService.queryMemberById(userId);
            if (user != null && StatusEnum.ENABLED.getKey().equals(user.getStatus())) {
                executeRulesForUser(user, null);
            }
        }
    }

    @Override
    public boolean checkUserMatchRule(MtUser user, MtUserTagRule rule) {
        String ruleType = rule.getRuleType();
        String timeRange = rule.getTimeRange();
        String operatorType = rule.getOperatorType();
        BigDecimal thresholdValue = rule.getThresholdValue();
        BigDecimal thresholdMax = rule.getThresholdMax();

        // 计算时间范围
        Date startTime = null;
        Date endTime = new Date();
        if (StringUtil.isNotEmpty(timeRange) && !"all".equals(timeRange)) {
            TagRuleTimeRangeEnum timeRangeEnum = getTimeRangeEnum(timeRange);
            if (timeRangeEnum != null && timeRangeEnum.getDays() > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -timeRangeEnum.getDays());
                startTime = cal.getTime();
            }
        }

        // 根据规则类型获取实际值
        BigDecimal actualValue = BigDecimal.ZERO;

        switch (ruleType) {
            case "consume_count":
                // 消费次数
                actualValue = new BigDecimal(getUserOrderCount(user.getId(), startTime, endTime));
                break;
            case "consume_amount":
                // 消费金额
                actualValue = getUserOrderAmount(user.getId(), startTime, endTime);
                break;
            case "last_consume":
                // 最后消费时间
                Date lastOrderTime = getUserLastOrderTime(user.getId());
                if (lastOrderTime != null) {
                    long days = (System.currentTimeMillis() - lastOrderTime.getTime()) / (1000 * 60 * 60 * 24);
                    actualValue = new BigDecimal(days);
                } else {
                    actualValue = new BigDecimal(99999); // 从未消费
                }
                break;
            case "register_time":
                // 注册时间
                if (user.getCreateTime() != null) {
                    long days = (System.currentTimeMillis() - user.getCreateTime().getTime()) / (1000 * 60 * 60 * 24);
                    actualValue = new BigDecimal(days);
                }
                break;
            case "single_order_amount":
                // 单笔订单金额
                actualValue = getUserMaxOrderAmount(user.getId(), startTime, endTime);
                break;
            case "avg_order_amount":
                // 平均订单金额
                actualValue = getUserAvgOrderAmount(user.getId(), startTime, endTime);
                break;
            case "total_order_count":
                // 累计订单数
                actualValue = new BigDecimal(getUserOrderCount(user.getId(), null, null));
                break;
            case "point_balance":
                // 积分余额
                actualValue = new BigDecimal(user.getPoint() == null ? 0 : user.getPoint());
                break;
            default:
                return false;
        }

        // 比较操作
        return compareValue(actualValue, thresholdValue, thresholdMax, operatorType);
    }

    /**
     * 获取时间范围枚举
     */
    private TagRuleTimeRangeEnum getTimeRangeEnum(String timeRange) {
        for (TagRuleTimeRangeEnum item : TagRuleTimeRangeEnum.values()) {
            if (item.getKey().equals(timeRange)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取会员订单数量
     */
    private Integer getUserOrderCount(Integer userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtOrder::getUserId, userId);
        wrapper.eq(MtOrder::getStatus, OrderStatusEnum.COMPLETE.getKey());
        if (startTime != null) {
            wrapper.ge(MtOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MtOrder::getCreateTime, endTime);
        }
        return mtOrderMapper.selectCount(wrapper).intValue();
    }

    /**
     * 获取会员订单总金额
     */
    private BigDecimal getUserOrderAmount(Integer userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtOrder::getUserId, userId);
        wrapper.eq(MtOrder::getStatus, OrderStatusEnum.COMPLETE.getKey());
        if (startTime != null) {
            wrapper.ge(MtOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MtOrder::getCreateTime, endTime);
        }
        List<MtOrder> orders = mtOrderMapper.selectList(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (MtOrder order : orders) {
            if (order.getPayAmount() != null) {
                total = total.add(order.getPayAmount());
            }
        }
        return total;
    }

    /**
     * 获取会员最后订单时间
     */
    private Date getUserLastOrderTime(Integer userId) {
        LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtOrder::getUserId, userId);
        wrapper.eq(MtOrder::getStatus, OrderStatusEnum.COMPLETE.getKey());
        wrapper.orderByDesc(MtOrder::getCreateTime);
        wrapper.last("LIMIT 1");
        MtOrder order = mtOrderMapper.selectOne(wrapper);
        return order != null ? order.getCreateTime() : null;
    }

    /**
     * 获取会员最大订单金额
     */
    private BigDecimal getUserMaxOrderAmount(Integer userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtOrder::getUserId, userId);
        wrapper.eq(MtOrder::getStatus, OrderStatusEnum.COMPLETE.getKey());
        if (startTime != null) {
            wrapper.ge(MtOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MtOrder::getCreateTime, endTime);
        }
        wrapper.orderByDesc(MtOrder::getPayAmount);
        wrapper.last("LIMIT 1");
        MtOrder order = mtOrderMapper.selectOne(wrapper);
        return order != null && order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO;
    }

    /**
     * 获取会员平均订单金额
     */
    private BigDecimal getUserAvgOrderAmount(Integer userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtOrder::getUserId, userId);
        wrapper.eq(MtOrder::getStatus, OrderStatusEnum.COMPLETE.getKey());
        if (startTime != null) {
            wrapper.ge(MtOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MtOrder::getCreateTime, endTime);
        }
        List<MtOrder> orders = mtOrderMapper.selectList(wrapper);
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (MtOrder order : orders) {
            if (order.getPayAmount() != null) {
                total = total.add(order.getPayAmount());
            }
        }
        return total.divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 比较数值
     */
    private boolean compareValue(BigDecimal actual, BigDecimal threshold, BigDecimal thresholdMax, String operator) {
        if (actual == null) {
            actual = BigDecimal.ZERO;
        }

        switch (operator) {
            case "gt":
                return actual.compareTo(threshold) > 0;
            case "gte":
                return actual.compareTo(threshold) >= 0;
            case "lt":
                return actual.compareTo(threshold) < 0;
            case "lte":
                return actual.compareTo(threshold) <= 0;
            case "eq":
                return actual.compareTo(threshold) == 0;
            case "between":
                return actual.compareTo(threshold) >= 0 && (thresholdMax == null || actual.compareTo(thresholdMax) <= 0);
            case "ne":
                return actual.compareTo(threshold) != 0;
            default:
                return false;
        }
    }
}
