package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.commission.CommissionLogDto;
import com.fuint.common.dto.commission.CommissionOverviewDto;
import com.fuint.common.dto.order.OrderUserDto;
import com.fuint.common.enums.*;
import com.fuint.common.param.CommissionLogPage;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 分销提成记录服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class CommissionLogServiceImpl extends ServiceImpl<MtCommissionLogMapper, MtCommissionLog> implements CommissionLogService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionLogServiceImpl.class);

    private MtCommissionLogMapper mtCommissionLogMapper;

    private MtCommissionRuleMapper mtCommissionRuleMapper;

    private MtCommissionRuleItemMapper mtCommissionRuleItemMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

    private MtCommissionRelationMapper mtCommissionRelationMapper;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 员工服务接口
     * */
    private StaffService staffService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 提成方案规则服务接口
     * */
    private CommissionRuleService commissionRuleService;

    /**
     * 提现记录 Mapper
     * */
    private MtCommissionCashMapper mtCommissionCashMapper;

    /**
     * 分页查询分销提成列表
     *
     * @param commissionLogPage
     * @return
     */
    @Override
    public PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(CommissionLogPage commissionLogPage) {
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());
        String target = commissionLogPage.getTarget();
        if (StringUtils.isNotBlank(target)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getTarget, target);
        }
        String realName = commissionLogPage.getRealName();
        if (StringUtils.isNotBlank(realName)) {
            Map<String, Object> params = new HashMap<>();
            params.put("REAL_NAME", realName);
            params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
            List<MtStaff> staffList = staffService.queryStaffByParams(params);
            if (staffList != null && staffList.size() > 0) {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, staffList.get(0).getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, -1);
            }
        }
        String mobile = commissionLogPage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            MtStaff mtStaff = staffService.queryStaffByMobile(mobile);
            if (mtStaff != null) {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, mtStaff.getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, -1);
            }
        }
        String uuid = commissionLogPage.getUuid();
        if (StringUtils.isNotBlank(uuid)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getSettleUuid, uuid);
        }
        String status = commissionLogPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStatus, status);
        }
        Integer merchantId = commissionLogPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        Integer storeId = commissionLogPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStoreId, storeId);
        }
        // 开始时间、结束时间
        String startTime = commissionLogPage.getStartTime();
        String endTime = commissionLogPage.getEndTime();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtCommissionLog::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtCommissionLog::getCreateTime, endTime);
        }

        lambdaQueryWrapper.orderByDesc(MtCommissionLog::getId);
        Page<MtCommissionLog> pageHelper = PageHelper.startPage(commissionLogPage.getPage(), commissionLogPage.getPageSize());
        List<MtCommissionLog> commissionLogList = mtCommissionLogMapper.selectList(lambdaQueryWrapper);
        List<CommissionLogDto> dataList = new ArrayList<>();
        if (commissionLogList != null && commissionLogList.size() > 0) {
            for (MtCommissionLog mtCommissionLog : commissionLogList) {
                 CommissionLogDto commissionLogDto = new CommissionLogDto();
                 BeanUtils.copyProperties(mtCommissionLog, commissionLogDto);
                 commissionLogDto.setTypeName(CommissionTypeEnum.getName(mtCommissionLog.getType()));
                 MtOrder mtOrder = orderService.getById(mtCommissionLog.getOrderId());
                 commissionLogDto.setOrderInfo(mtOrder);
                 MtStore mtStore = storeService.getById(mtCommissionLog.getStoreId());
                 commissionLogDto.setStoreInfo(mtStore);
                 MtStaff mtStaff = staffService.getById(mtCommissionLog.getStaffId());
                 if (mtStaff != null) {
                     mtStaff.setMobile(CommonUtil.hidePhone(mtStaff.getMobile()));
                     commissionLogDto.setStaffInfo(mtStaff);
                 }
                 MtCommissionRule mtCommissionRule = commissionRuleService.getById(mtCommissionLog.getRuleId());
                 commissionLogDto.setRuleInfo(mtCommissionRule);
                 if (mtCommissionLog.getUserId() != null && mtCommissionLog.getUserId() > 0) {
                     MtUser userInfo = memberService.queryMemberById(mtCommissionLog.getUserId());
                     if (userInfo != null) {
                         OrderUserDto userDto = new OrderUserDto();
                         userDto.setNo(userInfo.getUserNo());
                         userDto.setId(userInfo.getId());
                         userDto.setName(userInfo.getName());
                         userDto.setCardNo(userInfo.getIdcard());
                         userDto.setAddress(userInfo.getAddress());
                         userDto.setMobile(CommonUtil.hidePhone(userInfo.getMobile()));
                         commissionLogDto.setUserInfo(userDto);
                     }
                 }
                 dataList.add(commissionLogDto);
            }
        }
        PageRequest pageRequest = PageRequest.of(commissionLogPage.getPage(), commissionLogPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<CommissionLogDto> paginationResponse = new PaginationResponse(pageImpl, CommissionLogDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 获取佣金概览数据
     *
     * @param userId 会员ID
     * @return
     */
    @Override
    public CommissionOverviewDto getCommissionOverview(Integer userId) {
        CommissionOverviewDto overviewDto = new CommissionOverviewDto();

        // 总佣金（待结算佣金）
        BigDecimal totalAmount = mtCommissionLogMapper.getTotalCommissionAmount(userId);
        overviewDto.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

        // 已提现金额
        BigDecimal withdrawAmount = mtCommissionCashMapper.getWithdrawAmount(userId);
        overviewDto.setWithdrawAmount(withdrawAmount != null ? withdrawAmount : BigDecimal.ZERO);

        // 待提现金额 = 总佣金 - 已提现金额
        overviewDto.setAmount(overviewDto.getTotalAmount().subtract(overviewDto.getWithdrawAmount()));

        // 邀请会员数
        Long userCount = mtCommissionRelationMapper.getInvitedUserCount(userId);
        overviewDto.setUserCount(userCount != null ? new BigDecimal(userCount) : BigDecimal.ZERO);

        // 订单数
        Long orderCount = mtCommissionLogMapper.getCommissionOrderCount(userId);
        overviewDto.setOrderCount(orderCount != null ? new BigDecimal(orderCount) : BigDecimal.ZERO);

        return overviewDto;
    }

    /**
     * 计算订单分销提成
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "计算订单分销提成")
    public void calculateCommission(Integer orderId) {
        if (orderId != null && orderId > 0) {
            MtOrder mtOrder = orderService.getById(orderId);
            // 获取一级邀请关系
            Integer commissionUserId = mtCommissionRelationMapper.getCommissionUserId(mtOrder.getMerchantId(), mtOrder.getUserId());
            if (commissionUserId != null && commissionUserId > 0) {
                mtOrder.setCommissionUserId(commissionUserId);
                orderService.updateOrder(mtOrder);
            }
            // 获取二级邀请关系
            Integer secondLevelUserId = mtCommissionRelationMapper.getSecondLevelCommissionUserId(mtOrder.getMerchantId(), mtOrder.getUserId());

            // 判断是否为散客订单
            boolean isVisitorOrder = mtOrder.getStaffId() != null && mtOrder.getStaffId() > 0
                    && YesOrNoEnum.YES.getKey().equals(mtOrder.getIsVisitor());

            // 商品订单佣金计算
            if (mtOrder != null && mtOrder.getType().equals(CommissionTypeEnum.GOODS.getKey())) {
                Map<String, Object> params = new HashMap<>();
                params.put("ORDER_ID", mtOrder.getId());
                params.put("STATUS", StatusEnum.ENABLED.getKey());
                List<MtOrderGoods> goodsList = mtOrderGoodsMapper.selectByMap(params);
                if (goodsList != null && goodsList.size() > 0) {
                    for (MtOrderGoods orderGoods : goodsList) {
                         List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.getEffectiveCommissionList(mtOrder.getMerchantId(), orderGoods.getGoodsId(), CommissionTypeEnum.GOODS.getKey());
                         if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                             for (MtCommissionRuleItem mtCommissionRuleItem : commissionRuleItemList) {
                                  MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(mtCommissionRuleItem.getRuleId());
                                  // 规则状态正常
                                  if (mtCommissionRule != null && mtCommissionRule.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                                     if (orderGoods.getNum() == null || orderGoods.getNum() < 1) {
                                         orderGoods.setNum(1d);
                                     }
                                     // 一级分佣
                                     BigDecimal rate = isVisitorOrder ? mtCommissionRuleItem.getGuest() : mtCommissionRuleItem.getMember();
                                     BigDecimal amount = orderGoods.getPrice().multiply(rate.divide(new BigDecimal("100"))).multiply(new BigDecimal(orderGoods.getNum()));
                                     addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, mtOrder.getCommissionUserId(), 1);

                                     // 二级分佣（仅会员分销，且二级邀请关系存在）
                                     if (secondLevelUserId != null && secondLevelUserId > 0
                                             && CommissionTargetEnum.MEMBER.getKey().equals(mtCommissionRule.getTarget())) {
                                         BigDecimal secondRate = isVisitorOrder ? mtCommissionRuleItem.getSubGuest() : mtCommissionRuleItem.getSubMember();
                                         if (secondRate != null && secondRate.compareTo(BigDecimal.ZERO) > 0) {
                                             BigDecimal secondAmount = orderGoods.getPrice().multiply(secondRate.divide(new BigDecimal("100"))).multiply(new BigDecimal(orderGoods.getNum()));
                                             addCommissionLog(mtOrder, mtCommissionRule, secondAmount, mtCommissionRuleItem, secondLevelUserId, 2);
                                         }
                                     }
                                  }
                             }
                         }
                    }
                }
            }

            // 充值订单计算佣金
            if (mtOrder != null && mtOrder.getType().equals(CommissionTypeEnum.RECHARGE.getKey())) {
                List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.getEffectiveCommissionList(mtOrder.getMerchantId(), null, CommissionTypeEnum.RECHARGE.getKey());
                if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                    for (MtCommissionRuleItem mtCommissionRuleItem : commissionRuleItemList) {
                         MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(mtCommissionRuleItem.getRuleId());
                         // 一级分佣
                         BigDecimal rate = isVisitorOrder ? mtCommissionRuleItem.getGuest() : mtCommissionRuleItem.getMember();
                         BigDecimal amount = mtOrder.getAmount().multiply(rate.divide(new BigDecimal("100")));
                         addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, mtOrder.getCommissionUserId(), 1);

                         // 二级分佣（仅会员分销，且二级邀请关系存在）
                         if (secondLevelUserId != null && secondLevelUserId > 0
                                 && CommissionTargetEnum.MEMBER.getKey().equals(mtCommissionRule.getTarget())) {
                             BigDecimal secondRate = isVisitorOrder ? mtCommissionRuleItem.getSubGuest() : mtCommissionRuleItem.getSubMember();
                             if (secondRate != null && secondRate.compareTo(BigDecimal.ZERO) > 0) {
                                 BigDecimal secondAmount = mtOrder.getAmount().multiply(secondRate.divide(new BigDecimal("100")));
                                 addCommissionLog(mtOrder, mtCommissionRule, secondAmount, mtCommissionRuleItem, secondLevelUserId, 2);
                             }
                         }
                    }
                }
            }

            // 付款订单计算佣金
            if (mtOrder != null && mtOrder.getType().equals(CommissionTypeEnum.PAYMENT.getKey())) {
                List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.getEffectiveCommissionList(mtOrder.getMerchantId(), null, CommissionTypeEnum.PAYMENT.getKey());
                if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                    for (MtCommissionRuleItem mtCommissionRuleItem : commissionRuleItemList) {
                         MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(mtCommissionRuleItem.getRuleId());
                         // 一级分佣
                         BigDecimal rate = isVisitorOrder ? mtCommissionRuleItem.getGuest() : mtCommissionRuleItem.getMember();
                         BigDecimal amount = mtOrder.getPayAmount().multiply(rate.divide(new BigDecimal("100")));
                         addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, mtOrder.getCommissionUserId(), 1);

                         // 二级分佣（仅会员分销，且二级邀请关系存在）
                         if (secondLevelUserId != null && secondLevelUserId > 0
                                 && CommissionTargetEnum.MEMBER.getKey().equals(mtCommissionRule.getTarget())) {
                             BigDecimal secondRate = isVisitorOrder ? mtCommissionRuleItem.getSubGuest() : mtCommissionRuleItem.getSubMember();
                             if (secondRate != null && secondRate.compareTo(BigDecimal.ZERO) > 0) {
                                 BigDecimal secondAmount;
                                 if (mtOrder.getPayAmount() != null) {
                                     secondAmount = mtOrder.getPayAmount().multiply(secondRate.divide(new BigDecimal("100")));
                                 } else {
                                     secondAmount = mtOrder.getAmount().multiply(secondRate.divide(new BigDecimal("100")));
                                 }
                                 addCommissionLog(mtOrder, mtCommissionRule, secondAmount, mtCommissionRuleItem, secondLevelUserId, 2);
                             }
                         }
                    }
                }
            }

            // 订单更新为已结算
            if (mtOrder != null) {
                mtOrder.setCommissionStatus(CommissionStatusEnum.SETTLED.getKey());
                orderService.updateOrder(mtOrder);
            }
        } else {
            logger.error("计算分销提成订单不能ID为空...");
        }
    }

    /**
     * 根据ID获取记录信息
     *
     * @param id 分佣提成记录ID
     * @return
     */
    @Override
    public CommissionLogDto queryCommissionLogById(Integer id) {
        MtCommissionLog mtCommissionLog = mtCommissionLogMapper.selectById(id);
        CommissionLogDto commissionLogDto = null;
        if (mtCommissionLog != null) {
            BeanUtils.copyProperties(mtCommissionLog, commissionLogDto);
        }
        return commissionLogDto;
    }

    /**
     * 更新分销提成记录
     *
     * @param requestParam 请求参数
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新分销提成记录")
    public void updateCommissionLog(CommissionLogRequest requestParam) throws BusinessCheckException {
        MtCommissionLog mtCommissionLog =  mtCommissionLogMapper.selectById(requestParam.getId());
        if (mtCommissionLog == null) {
            logger.error("更新分销提成记录失败...");
            throw new BusinessCheckException("更新分销提成记录失败，该记录不存在");
        }
        if (requestParam.getAmount() != null) {
            mtCommissionLog.setAmount(new BigDecimal(requestParam.getAmount()));
        }
        if (requestParam.getDescription() != null) {
            mtCommissionLog.setDescription(requestParam.getDescription());
        }
        if (requestParam.getStatus() != null) {
            mtCommissionLog.setStatus(requestParam.getStatus());
        }
        if (requestParam.getSettleUuid() != null) {
            mtCommissionLog.setSettleUuid(requestParam.getSettleUuid());
        }
        mtCommissionLog.setOperator(requestParam.getOperator());
        mtCommissionLog.setUpdateTime(new Date());
        mtCommissionLogMapper.updateById(mtCommissionLog);
    }

    /**
     * 新增分佣记录
     *
     * @param mtOrder 订单信息
     * @param mtCommissionRule 分佣方案
     * @param amount 分佣金额
     * @param mtCommissionRuleItem 分佣规则
     * @param userId 会员ID
     * @param level 分佣等级（1=一级，2=二级）
     * @return
     * */
    @Transactional
    @OperationServiceLog(description = "新增分销提成记录")
    public void addCommissionLog(MtOrder mtOrder, MtCommissionRule mtCommissionRule, BigDecimal amount, MtCommissionRuleItem mtCommissionRuleItem, Integer userId, Integer level) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            MtCommissionLog mtCommissionLog = new MtCommissionLog();
            mtCommissionLog.setType(mtOrder.getType());
            mtCommissionLog.setTarget(mtCommissionRule.getTarget());
            mtCommissionLog.setLevel(level);
            mtCommissionLog.setOrderId(mtOrder.getId());
            mtCommissionLog.setMerchantId(mtOrder.getMerchantId());
            mtCommissionLog.setStoreId(mtOrder.getStoreId());
            if (mtCommissionRule.getTarget().equals(CommissionTargetEnum.STAFF.getKey())) {
                if (mtOrder.getStaffId() == null || mtOrder.getStaffId() <= 0) {
                    return;
                }
                mtCommissionLog.setStaffId(mtOrder.getStaffId());
                mtCommissionLog.setUserId(0);
            } else {
                if (userId == null || userId <= 0) {
                    return;
                }
                mtCommissionLog.setStaffId(0);
                mtCommissionLog.setUserId(userId);
            }
            mtCommissionLog.setAmount(amount);
            mtCommissionLog.setRuleId(mtCommissionRuleItem.getRuleId());
            mtCommissionLog.setRuleItemId(mtCommissionRuleItem.getId());
            mtCommissionLog.setCashId(0);
            mtCommissionLog.setCashTime(null);
            Date dateTime = new Date();
            mtCommissionLog.setCreateTime(dateTime);
            mtCommissionLog.setUpdateTime(dateTime);
            mtCommissionLog.setStatus(StatusEnum.ENABLED.getKey());
            mtCommissionLog.setOperator(null);
            mtCommissionLogMapper.insert(mtCommissionLog);
        }
    }
}
