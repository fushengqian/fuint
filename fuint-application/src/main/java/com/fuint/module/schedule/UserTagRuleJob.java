package com.fuint.module.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.common.service.UserTagRuleService;
import com.fuint.common.util.RedisLock;
import com.fuint.common.util.SeqUtil;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserTagRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 会员标签规则定时任务
 * 每天凌晨2点自动执行所有商户的会员标签规则
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("userTagRuleJob")
public class UserTagRuleJob {

    private static final Logger logger = LoggerFactory.getLogger(UserTagRuleJob.class);

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private UserTagRuleService userTagRuleService;

    @Autowired
    private UserTagRelationService userTagRelationService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private Environment environment;

    /**
     * 每天凌晨2点执行会员标签规则
     * 0 0 2 * * ? = 每天凌晨2点
     */
    @Scheduled(cron = "${userTagRule.job.time:0 0 2 * * ?}")
    public void executeUserTagRules() {
        String lockKey = "lock:userTagRuleJob:execute";
        String requestId = SeqUtil.getUUID();
        boolean locked = false;

        try {
            // 尝试加锁，30分钟自动过期
            locked = redisLock.tryLock(lockKey, requestId, 1800);
            if (!locked) {
                logger.info("UserTagRuleJob 正在执行中，跳过本次任务");
                return;
            }

            String theSwitch = environment.getProperty("userTagRule.job.switch");
            if (theSwitch != null && theSwitch.equals("1")) {
                logger.info("========== UserTagRuleJob 开始执行 ==========");
                long startTime = System.currentTimeMillis();

                // 获取所有启用状态的商户
                LambdaQueryWrapper<MtMerchant> merchantWrapper = new LambdaQueryWrapper<>();
                merchantWrapper.eq(MtMerchant::getStatus, StatusEnum.ENABLED.getKey());
                List<MtMerchant> merchantList = merchantService.list(merchantWrapper);

                int successCount = 0;
                int failCount = 0;

                for (MtMerchant merchant : merchantList) {
                    try {
                        executeRulesForMerchant(merchant.getId());
                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                        logger.error("处理商户[{}]标签规则失败: {}", merchant.getId(), e.getMessage());
                    }
                }

                long endTime = System.currentTimeMillis();
                logger.info("========== UserTagRuleJob 执行完成 ==========");
                logger.info("处理商户数: {}, 成功: {}, 失败: {}, 耗时: {}ms", 
                    merchantList.size(), successCount, failCount, (endTime - startTime));
            } else {
                logger.info("UserTagRuleJob 开关未开启，跳过执行");
            }
        } catch (Exception e) {
            logger.error("UserTagRuleJob 执行异常: {}", e.getMessage(), e);
        } finally {
            // 释放分布式锁
            if (locked) {
                redisLock.unlock(lockKey, requestId);
            }
        }
    }

    /**
     * 执行单个商户的标签规则
     */
    private void executeRulesForMerchant(Integer merchantId) {
        // 获取商户启用的自动规则
        List<MtUserTagRule> rules = userTagRuleService.getMerchantRuleList(merchantId, StatusEnum.ENABLED.getKey());
        if (rules.isEmpty()) {
            return;
        }

        // 过滤出isAuto为Y的规则
        rules = rules.stream()
            .filter(rule -> "Y".equals(rule.getIsAuto()))
            .collect(Collectors.toList());

        if (rules.isEmpty()) {
            return;
        }

        logger.info("商户[{}]有{}条自动标签规则需要执行", merchantId, rules.size());

        // 获取该商户所有会员
        List<Integer> userIds = memberService.getUserIdList(merchantId, null);
        if (userIds.isEmpty()) {
            return;
        }

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setMerchantId(merchantId);
        accountInfo.setAccountName("system");

        for (Integer userId : userIds) {
            MtUser user = memberService.queryMemberById(userId);
            if (user != null && StatusEnum.ENABLED.getKey().equals(user.getStatus())) {
                executeRulesForUser(user, rules, accountInfo);
            }
        }

        logger.info("商户[{}]会员标签规则执行完成", merchantId);
    }

    /**
     * 执行单个会员的标签规则
     */
    private void executeRulesForUser(MtUser user, List<MtUserTagRule> rules, AccountInfo accountInfo) {
        if (user == null || user.getMerchantId() == null) {
            return;
        }

        // 获取所有自动规则管理的标签ID集合
        Set<Integer> autoRuleTagIds = rules.stream()
                .map(MtUserTagRule::getTagId)
                .collect(Collectors.toSet());

        // 获取会员已有标签
        List<Integer> existTagIds = userTagRelationService.getTagIdsByUserId(user.getId());

        // 重建新标签列表：从空开始，而非从已有标签开始
        List<Integer> newTagIds = new ArrayList<>();

        // 保留手动设置的标签（不在任何自动规则中的标签）
        for (Integer existTagId : existTagIds) {
            if (!autoRuleTagIds.contains(existTagId)) {
                newTagIds.add(existTagId);
            }
        }

        // 根据规则匹配结果添加标签
        for (MtUserTagRule rule : rules) {
            try {
                boolean isMatch = checkUserMatchRule(user, rule);
                if (isMatch) {
                    if (!newTagIds.contains(rule.getTagId())) {
                        newTagIds.add(rule.getTagId());
                        logger.debug("会员[{}]符合规则[{}]，添加标签[{}]", user.getId(), rule.getRuleName(), rule.getTagId());
                    }
                } else {
                    logger.debug("会员[{}]不再符合规则[{}]，标签[{}]将被移除", user.getId(), rule.getRuleName(), rule.getTagId());
                }
            } catch (Exception e) {
                logger.error("执行规则[{}]异常: {}", rule.getId(), e.getMessage());
            }
        }

        // 更新会员标签（只要标签有变化就更新）
        Set<Integer> newSet = new HashSet<>(newTagIds);
        Set<Integer> existSet = new HashSet<>(existTagIds);
        if (!newSet.equals(existSet)) {
            userTagRelationService.setUserTags(user.getId(), newTagIds, accountInfo.getAccountName());
            logger.info("会员[{}]标签已更新，原标签: {} -> 新标签: {}", user.getId(), existTagIds, newTagIds);
        }
    }

    /**
     * 检查会员是否符合规则
     */
    private boolean checkUserMatchRule(MtUser user, MtUserTagRule rule) {
        if (rule == null) {
            return false;
        }
        // 委托给 UserTagRuleService 执行
        return userTagRuleService.checkUserMatchRule(user, rule);
    }
}
