package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuint.repository.mapper.MtCommissionCashMapper;
import com.fuint.repository.mapper.MtCommissionLogMapper;
import com.fuint.repository.mapper.MtCommissionRelationMapper;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.mapper.MtStaffMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtCommissionCash;
import com.fuint.repository.model.MtCommissionLog;
import com.fuint.repository.model.MtCommissionRelation;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.common.service.CommissionReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分佣提成报表统计服务实现
 * 统计口径：
 * 佣金记录 mt_commission_log：rewardType='commission'，status A待结算/B已结算计入，C作废/D追回不计；
 * 提现记录 mt_commission_cash：A待审核/B审核通过/C作废/D已打款；
 * 总提现金额=全部申请，已提现=D，待提现=A+B（未打款），待审核佣金金额=A；
 * 邀请人数=mt_commission_relation 中该会员一级邀请且状态有效的累计人数。
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class CommissionReportServiceImpl implements CommissionReportService {

    private MtCommissionLogMapper commissionLogMapper;

    private MtCommissionCashMapper commissionCashMapper;

    private MtCommissionRelationMapper commissionRelationMapper;

    private MtStaffMapper staffMapper;

    private MtUserMapper userMapper;

    private MtOrderMapper orderMapper;

    private static final String REWARD_COMMISSION = "commission";
    private static final String TARGET_MEMBER = "member";
    private static final String TARGET_STAFF = "staff";
    private static final int RANK_SIZE = 30;

    @Override
    public Map<String, Object> getOverview(Integer merchantId) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<MtCommissionLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.select(MtCommissionLog::getId, MtCommissionLog::getUserId, MtCommissionLog::getStatus, MtCommissionLog::getTarget);
        if (merchantId != null && merchantId > 0) {
            logWrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        List<MtCommissionLog> logList = commissionLogMapper.selectList(logWrapper);

        LambdaQueryWrapper<MtCommissionCash> cashWrapper = new LambdaQueryWrapper<>();
        cashWrapper.select(MtCommissionCash::getId, MtCommissionCash::getAmount, MtCommissionCash::getStatus);
        if (merchantId != null && merchantId > 0) {
            cashWrapper.eq(MtCommissionCash::getMerchantId, merchantId);
        }
        List<MtCommissionCash> cashList = commissionCashMapper.selectList(cashWrapper);

        int totalCount = logList.size();
        int waitAuditCount = 0;
        Set<Integer> commissionUserIds = new HashSet<>();
        for (MtCommissionLog log : logList) {
            if ("A".equals(log.getStatus())) {
                waitAuditCount++;
            }
            if (log.getUserId() != null) {
                commissionUserIds.add(log.getUserId());
            }
        }

        BigDecimal totalCashAmount = BigDecimal.ZERO;
        BigDecimal cashedAmount = BigDecimal.ZERO;
        BigDecimal waitCashAmount = BigDecimal.ZERO;
        BigDecimal waitAuditAmount = BigDecimal.ZERO;
        for (MtCommissionCash cash : cashList) {
            BigDecimal amount = cash.getAmount() == null ? BigDecimal.ZERO : cash.getAmount();
            totalCashAmount = totalCashAmount.add(amount);
            String status = cash.getStatus() == null ? "" : cash.getStatus();
            if ("D".equals(status)) {
                cashedAmount = cashedAmount.add(amount);
            } else if ("A".equals(status) || "B".equals(status)) {
                waitCashAmount = waitCashAmount.add(amount);
                if ("A".equals(status)) {
                    waitAuditAmount = waitAuditAmount.add(amount);
                }
            }
        }

        result.put("totalCommissionCount", totalCount);
        result.put("waitAuditCount", waitAuditCount);
        result.put("totalCashAmount", totalCashAmount);
        result.put("cashedAmount", cashedAmount);
        result.put("waitCashAmount", waitCashAmount);
        result.put("waitAuditAmount", waitAuditAmount);
        result.put("commissionUserCount", commissionUserIds.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrend(Integer merchantId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        LambdaQueryWrapper<MtCommissionLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(MtCommissionLog::getAmount, MtCommissionLog::getCreateTime);
        wrapper.eq(MtCommissionLog::getRewardType, REWARD_COMMISSION);
        wrapper.in(MtCommissionLog::getStatus, "A", "B");
        wrapper.ge(MtCommissionLog::getCreateTime, toDate(startDate));
        if (merchantId != null && merchantId > 0) {
            wrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        List<MtCommissionLog> logList = commissionLogMapper.selectList(wrapper);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, BigDecimal> dayAmount = new HashMap<>();
        for (MtCommissionLog log : logList) {
            if (log.getCreateTime() == null) {
                continue;
            }
            String day = log.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);
            BigDecimal amount = log.getAmount() == null ? BigDecimal.ZERO : log.getAmount();
            dayAmount.merge(day, amount, BigDecimal::add);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate day = startDate; !day.isAfter(endDate); day = day.plusDays(1)) {
            String key = day.format(formatter);
            Map<String, Object> item = new HashMap<>();
            item.put("date", key);
            item.put("amount", dayAmount.getOrDefault(key, BigDecimal.ZERO));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMemberRank(Integer merchantId, String month) {
        LambdaQueryWrapper<MtCommissionLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(MtCommissionLog::getUserId, MtCommissionLog::getOrderId, MtCommissionLog::getAmount,
                MtCommissionLog::getCouponNum, MtCommissionLog::getPoint);
        wrapper.eq(MtCommissionLog::getTarget, TARGET_MEMBER);
        wrapper.eq(MtCommissionLog::getRewardType, REWARD_COMMISSION);
        wrapper.in(MtCommissionLog::getStatus, "A", "B");
        wrapper.isNotNull(MtCommissionLog::getUserId);
        if (merchantId != null && merchantId > 0) {
            wrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        Date[] range = getMonthRange(month);
        if (range != null) {
            wrapper.ge(MtCommissionLog::getCreateTime, range[0]);
            wrapper.lt(MtCommissionLog::getCreateTime, range[1]);
        }
        List<MtCommissionLog> logList = commissionLogMapper.selectList(wrapper);

        // userId -> [amount, orderIdSet, couponNum, point]
        Map<Integer, BigDecimal> amountMap = new LinkedHashMap<>();
        Map<Integer, Set<Integer>> orderMap = new LinkedHashMap<>();
        Map<Integer, Integer> couponMap = new LinkedHashMap<>();
        Map<Integer, Integer> pointMap = new LinkedHashMap<>();
        for (MtCommissionLog log : logList) {
            Integer userId = log.getUserId();
            if (userId == null) {
                continue;
            }
            BigDecimal amount = log.getAmount() == null ? BigDecimal.ZERO : log.getAmount();
            amountMap.merge(userId, amount, BigDecimal::add);
            if (log.getOrderId() != null) {
                orderMap.computeIfAbsent(userId, k -> new HashSet<>()).add(log.getOrderId());
            }
            if (log.getCouponNum() != null) {
                couponMap.merge(userId, log.getCouponNum(), Integer::sum);
            }
            if (log.getPoint() != null) {
                pointMap.merge(userId, log.getPoint(), Integer::sum);
            }
        }

        List<Integer> sortedUsers = new ArrayList<>(amountMap.keySet());
        sortedUsers.sort((a, b) -> amountMap.get(b).compareTo(amountMap.get(a)));
        if (sortedUsers.size() > RANK_SIZE) {
            sortedUsers = sortedUsers.subList(0, RANK_SIZE);
        }
        if (sortedUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 会员信息
        Map<Integer, MtUser> userMap = new HashMap<>();
        List<MtUser> userList = userMapper.selectBatchIds(sortedUsers);
        for (MtUser user : userList) {
            userMap.put(user.getId(), user);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        for (Integer userId : sortedUsers) {
            Map<String, Object> item = new HashMap<>();
            MtUser user = userMap.get(userId);
            item.put("rank", rank++);
            item.put("userId", userId);
            item.put("userName", user == null ? "" : user.getName());
            item.put("avatar", user == null ? "" : (user.getAvatar() == null ? "" : user.getAvatar()));
            item.put("mobile", user == null ? "" : (user.getMobile() == null ? "" : user.getMobile()));
            item.put("inviteCount", getInviteCount(merchantId, userId));
            item.put("orderCount", orderMap.containsKey(userId) ? orderMap.get(userId).size() : 0);
            item.put("amount", amountMap.get(userId));
            item.put("couponNum", couponMap.getOrDefault(userId, 0));
            item.put("point", pointMap.getOrDefault(userId, 0));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getStaffRank(Integer merchantId, String month) {
        LambdaQueryWrapper<MtStaff> staffWrapper = new LambdaQueryWrapper<>();
        if (merchantId != null && merchantId > 0) {
            staffWrapper.eq(MtStaff::getMerchantId, merchantId);
        }
        List<MtStaff> staffList = staffMapper.selectList(staffWrapper);

        LambdaQueryWrapper<MtCommissionLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(MtCommissionLog::getStaffId, MtCommissionLog::getOrderId, MtCommissionLog::getAmount);
        wrapper.eq(MtCommissionLog::getTarget, TARGET_STAFF);
        wrapper.eq(MtCommissionLog::getRewardType, REWARD_COMMISSION);
        wrapper.in(MtCommissionLog::getStatus, "A", "B");
        wrapper.isNotNull(MtCommissionLog::getStaffId);
        if (merchantId != null && merchantId > 0) {
            wrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        Date[] range = getMonthRange(month);
        if (range != null) {
            wrapper.ge(MtCommissionLog::getCreateTime, range[0]);
            wrapper.lt(MtCommissionLog::getCreateTime, range[1]);
        }
        List<MtCommissionLog> logList = commissionLogMapper.selectList(wrapper);

        // staffId -> 佣金、订单集合
        Map<Integer, BigDecimal> amountMap = new LinkedHashMap<>();
        Map<Integer, Set<Integer>> orderMap = new LinkedHashMap<>();
        Set<Integer> allOrderIds = new HashSet<>();
        for (MtCommissionLog log : logList) {
            Integer staffId = log.getStaffId();
            if (staffId == null) {
                continue;
            }
            BigDecimal amount = log.getAmount() == null ? BigDecimal.ZERO : log.getAmount();
            amountMap.merge(staffId, amount, BigDecimal::add);
            if (log.getOrderId() != null) {
                orderMap.computeIfAbsent(staffId, k -> new HashSet<>()).add(log.getOrderId());
                allOrderIds.add(log.getOrderId());
            }
        }

        // 订单实付金额
        Map<Integer, BigDecimal> orderAmountMap = new HashMap<>();
        if (!allOrderIds.isEmpty()) {
            LambdaQueryWrapper<MtOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.select(MtOrder::getId, MtOrder::getPayAmount).in(MtOrder::getId, allOrderIds);
            List<MtOrder> orderList = orderMapper.selectList(orderWrapper);
            for (MtOrder order : orderList) {
                BigDecimal payAmount = order.getPayAmount() == null ? BigDecimal.ZERO : order.getPayAmount();
                orderAmountMap.merge(order.getId(), payAmount, BigDecimal::add);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        for (MtStaff staff : staffList) {
            Integer staffId = staff.getId();
            BigDecimal commission = amountMap.getOrDefault(staffId, BigDecimal.ZERO);
            Set<Integer> orderIds = orderMap.getOrDefault(staffId, new HashSet<>());
            BigDecimal orderAmount = BigDecimal.ZERO;
            for (Integer orderId : orderIds) {
                orderAmount = orderAmount.add(orderAmountMap.getOrDefault(orderId, BigDecimal.ZERO));
            }
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("staffId", staffId);
            item.put("staffName", staff.getRealName() == null ? "" : staff.getRealName());
            item.put("mobile", staff.getMobile() == null ? "" : staff.getMobile());
            item.put("orderCount", orderIds.size());
            item.put("orderAmount", orderAmount);
            item.put("amount", commission);
            result.add(item);
        }
        result.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));
        for (int i = 0; i < result.size(); i++) {
            result.get(i).put("rank", i + 1);
        }
        return result;
    }

    /**
     * 累计邀请人数（一级邀请且状态有效）
     */
    private Integer getInviteCount(Integer merchantId, Integer userId) {
        LambdaQueryWrapper<MtCommissionRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtCommissionRelation::getUserId, userId);
        wrapper.eq(MtCommissionRelation::getLevel, 1);
        wrapper.eq(MtCommissionRelation::getStatus, "A");
        if (merchantId != null && merchantId > 0) {
            wrapper.eq(MtCommissionRelation::getMerchantId, merchantId);
        }
        return commissionRelationMapper.selectCount(wrapper);
    }

    /**
     * 解析月份区间，month 为空返回 null（统计全部）
     */
    private Date[] getMonthRange(String month) {
        if (month == null || month.trim().length() != 7) {
            return null;
        }
        try {
            LocalDate start = LocalDate.parse(month.trim() + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new Date[]{toDate(start), toDate(start.plusMonths(1))};
        } catch (Exception e) {
            return null;
        }
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
