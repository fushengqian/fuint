package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.commission.CommissionLogDto;
import com.fuint.common.dto.commission.CommissionOverviewDto;
import com.fuint.common.dto.order.OrderUserDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.*;
import com.fuint.common.param.CommissionLogPage;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
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
import org.springframework.core.env.Environment;
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
     * 会员卡券 Mapper（追回时按发放批次号定位券）
     * */
    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 积分服务接口
     * */
    private PointService pointService;

    /**
     * 系统环境变量
     * */
    private Environment environment;

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
        String scene = commissionLogPage.getScene();
        if (StringUtils.isNotBlank(scene)) {
            if ("reward".equals(scene)) {
                // 会员端推荐奖励：注册奖励 + 交易奖励
                lambdaQueryWrapper.in(MtCommissionLog::getScene, Arrays.asList(RewardSceneEnum.REGISTER.getKey(), RewardSceneEnum.ORDER.getKey()));
            } else {
                lambdaQueryWrapper.eq(MtCommissionLog::getScene, scene);
            }
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
        Integer userId = commissionLogPage.getUserId();
        if (userId != null && userId > 0) {
            lambdaQueryWrapper.eq(MtCommissionLog::getUserId, userId);
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
    public void calculateCommission(Integer orderId) throws BusinessCheckException {
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
                                  // 规则状态正常且在活动有效期内
                                  if (mtCommissionRule != null && mtCommissionRule.getStatus().equals(StatusEnum.ENABLED.getKey()) && isRuleInPeriod(mtCommissionRule)) {
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
                         // 规则状态正常且在活动有效期内
                         if (mtCommissionRule == null || !mtCommissionRule.getStatus().equals(StatusEnum.ENABLED.getKey()) || !isRuleInPeriod(mtCommissionRule)) {
                             continue;
                         }
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
                         // 规则状态正常且在活动有效期内
                         if (mtCommissionRule == null || !mtCommissionRule.getStatus().equals(StatusEnum.ENABLED.getKey()) || !isRuleInPeriod(mtCommissionRule)) {
                             continue;
                         }
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

    /**
     * 被推荐人注册后，给一级邀请人发放注册奖励（券/积分）
     *
     * 幂等：同规则项 + 同邀请人 + 同被推荐人只发一次
     *
     * @param inviteeUserId 被推荐人会员ID
     * @return
     */
    @Override
    public void grantRegisterReward(Integer inviteeUserId) {
        try {
            if (inviteeUserId == null || inviteeUserId <= 0 || !rewardEnabled()) {
                return;
            }
            MtUser invitee = memberService.queryMemberById(inviteeUserId);
            if (invitee == null || !StatusEnum.ENABLED.getKey().equals(invitee.getStatus())) {
                return;
            }
            Integer inviterId = mtCommissionRelationMapper.getCommissionUserId(invitee.getMerchantId(), inviteeUserId);
            if (inviterId == null || inviterId <= 0) {
                return;
            }
            MtUser inviter = memberService.queryMemberById(inviterId);
            if (inviter == null || !StatusEnum.ENABLED.getKey().equals(inviter.getStatus())) {
                return;
            }
            List<MtCommissionRule> rules = queryActiveRecommendRules(invitee.getMerchantId(), invitee.getStoreId());
            for (MtCommissionRule rule : rules) {
                if (!"Y".equals(rule.getRegisterSwitch())) {
                    continue;
                }
                List<MtCommissionRuleItem> items = queryRuleItems(rule.getId());
                for (MtCommissionRuleItem item : items) {
                    if (RewardSceneEnum.REGISTER.getKey().equals(item.getScene())) {
                        createAndGrantRewardLog(rule, item, inviter, inviteeUserId, inviteeUserId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("发放推荐注册奖励异常，inviteeUserId = {}", inviteeUserId, e);
        }
    }

    /**
     * 订单支付成功后，给一级邀请人发放交易奖励（券/积分）
     *
     * 幂等：同规则项 + 同邀请人 + 同订单只发一次
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public void grantOrderReward(Integer orderId) {
        try {
            if (orderId == null || orderId <= 0 || !rewardEnabled()) {
                return;
            }
            MtOrder mtOrder = orderService.getById(orderId);
            if (mtOrder == null || !PayStatusEnum.SUCCESS.getKey().equals(mtOrder.getPayStatus())) {
                return;
            }
            Integer inviterId = mtCommissionRelationMapper.getCommissionUserId(mtOrder.getMerchantId(), mtOrder.getUserId());
            if (inviterId == null || inviterId <= 0) {
                return;
            }
            MtUser inviter = memberService.queryMemberById(inviterId);
            if (inviter == null || !StatusEnum.ENABLED.getKey().equals(inviter.getStatus())) {
                return;
            }
            BigDecimal payAmount = mtOrder.getPayAmount() == null ? BigDecimal.ZERO : mtOrder.getPayAmount();
            List<MtCommissionRule> rules = queryActiveRecommendRules(mtOrder.getMerchantId(), mtOrder.getStoreId());
            for (MtCommissionRule rule : rules) {
                if (!"Y".equals(rule.getOrderSwitch())) {
                    continue;
                }
                BigDecimal threshold = rule.getOrderAmount() == null ? BigDecimal.ZERO : rule.getOrderAmount();
                if (threshold.compareTo(BigDecimal.ZERO) > 0 && payAmount.compareTo(threshold) < 0) {
                    continue;
                }
                List<MtCommissionRuleItem> items = queryRuleItems(rule.getId());
                for (MtCommissionRuleItem item : items) {
                    if (RewardSceneEnum.ORDER.getKey().equals(item.getScene())) {
                        createAndGrantRewardLog(rule, item, inviter, mtOrder.getUserId(), orderId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("发放推荐交易奖励异常，orderId = {}", orderId, e);
        }
    }

    /**
     * 发放单条奖励记录：成功置 B（已发放），失败置 C（发放失败）交由定时任务重试
     *
     * @param logId 奖励记录ID
     * @return
     */
    @Override
    public void grantRewardLog(Integer logId) {
        try {
            MtCommissionLog log = mtCommissionLogMapper.selectById(logId);
            if (log == null) {
                return;
            }
            if (!CommissionStatusEnum.NORMAL.getKey().equals(log.getStatus())
                    && !CommissionStatusEnum.CANCEL.getKey().equals(log.getStatus())) {
                return;
            }
            if (RewardTypeEnum.COUPON.getKey().equals(log.getRewardType())) {
                AccountInfo accountInfo = new AccountInfo();
                accountInfo.setMerchantId(log.getMerchantId());
                accountInfo.setAccountName("系统");
                String uuid = "RECOMMEND_" + log.getId();
                ResponseObject result = couponService.sendCoupon(log.getCouponId(), log.getUserId(),
                        log.getCouponNum() == null || log.getCouponNum() < 1 ? 1 : log.getCouponNum(), false, uuid, accountInfo);
                if (result.getCode() == null || result.getCode() != 200) {
                    throw new BusinessCheckException(result.getMessage());
                }
                log.setCouponUuid(uuid);
            } else if (RewardTypeEnum.POINT.getKey().equals(log.getRewardType())) {
                MtPoint mtPoint = new MtPoint();
                mtPoint.setUserId(log.getUserId());
                mtPoint.setAmount(log.getPoint() == null ? 0 : log.getPoint());
                mtPoint.setDescription("推荐奖励-" + RewardSceneEnum.getName(log.getScene()));
                mtPoint.setOperator("系统");
                pointService.addPoint(mtPoint);
            } else {
                return;
            }
            log.setStatus(CommissionStatusEnum.SETTLED.getKey());
            log.setUpdateTime(new Date());
            mtCommissionLogMapper.updateById(log);
            logger.info("推荐奖励发放成功，logId = {}, userId = {}, rewardType = {}", log.getId(), log.getUserId(), log.getRewardType());
        } catch (Exception e) {
            MtCommissionLog log = mtCommissionLogMapper.selectById(logId);
            if (log != null) {
                logger.error("推荐奖励发放失败，logId = {}, 原因：{}", logId, e.getMessage());
                String remark = e.getMessage() == null ? "发放失败" : e.getMessage();
                if (remark.length() > 200) {
                    remark = remark.substring(0, 200);
                }
                log.setStatus(CommissionStatusEnum.CANCEL.getKey());
                log.setDescription(remark);
                log.setRetryCount(log.getRetryCount() == null ? 1 : log.getRetryCount() + 1);
                log.setLastRetryTime(new Date());
                log.setUpdateTime(new Date());
                mtCommissionLogMapper.updateById(log);
            }
        }
    }

    /**
     * 重试待发放/发放失败的推荐奖励记录（CommissionJob 调用）
     *
     * @return
     */
    @Override
    public void retryRewards() {
        if (!rewardEnabled()) {
            return;
        }
        LambdaQueryWrapper<MtCommissionLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MtCommissionLog::getScene, "recommend");
        wrapper.in(MtCommissionLog::getStatus, Arrays.asList(CommissionStatusEnum.NORMAL.getKey(), CommissionStatusEnum.CANCEL.getKey()));
        wrapper.lt(MtCommissionLog::getRetryCount, 3);
        wrapper.orderByAsc(MtCommissionLog::getId);
        wrapper.last("LIMIT 10");
        List<MtCommissionLog> logList = mtCommissionLogMapper.selectList(wrapper);
        for (MtCommissionLog log : logList) {
            grantRewardLog(log.getId());
        }
    }

    /**
     * 退款前校验该订单的推荐奖励是否可追回
     *
     * @param orderId 订单ID
     * @return canRefund / blockers / rewards
     */
    @Override
    public Map<String, Object> checkRewardClawback(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        List<String> blockers = new ArrayList<>();
        List<Map<String, Object>> rewards = new ArrayList<>();
        result.put("canRefund", true);
        result.put("blockers", blockers);
        result.put("rewards", rewards);
        for (MtCommissionLog log : queryOrderRewardLogs(orderId)) {
            Map<String, Object> info = new HashMap<>();
            info.put("logId", log.getId());
            info.put("rewardType", log.getRewardType());
            if (RewardTypeEnum.COUPON.getKey().equals(log.getRewardType())) {
                MtCoupon coupon = couponService.queryCouponById(log.getCouponId());
                String name = coupon != null ? coupon.getName() : ("券#" + log.getCouponId());
                int num = log.getCouponNum() == null || log.getCouponNum() < 1 ? 1 : log.getCouponNum();
                info.put("text", name + " ×" + num);
                List<MtUserCoupon> userCoupons = mtUserCouponMapper.getUserCouponListByUuid(log.getCouponUuid());
                boolean used = false;
                if (userCoupons != null) {
                    for (MtUserCoupon userCoupon : userCoupons) {
                        if (UserCouponStatusEnum.USED.getKey().equals(userCoupon.getStatus())) {
                            used = true;
                            break;
                        }
                    }
                }
                info.put("clawable", !used);
                if (used) {
                    blockers.add(name + " ×" + num + " 已被推荐人使用，无法追回");
                }
            } else if (RewardTypeEnum.POINT.getKey().equals(log.getRewardType())) {
                int point = log.getPoint() == null ? 0 : log.getPoint();
                info.put("text", "积分 " + point);
                MtUser inviter = memberService.queryMemberById(log.getUserId());
                int balance = inviter == null || inviter.getPoint() == null ? 0 : inviter.getPoint();
                info.put("clawable", balance >= point);
                if (balance < point) {
                    blockers.add("积分 " + point + " 无法扣回：推荐人当前积分 " + balance + "，不足 " + point);
                }
            } else {
                continue;
            }
            rewards.add(info);
        }
        result.put("canRefund", blockers.isEmpty());
        return result;
    }

    /**
     * 退款成功后追回该订单的推荐奖励：未使用券作废、积分扣回、过期券仅记录
     *
     * @param orderId 订单ID
     * @param operator 操作人
     * @return
     */
    @Override
    public void clawbackOrderReward(Integer orderId, String operator) {
        List<MtCommissionLog> logList = queryOrderRewardLogs(orderId);
        for (MtCommissionLog log : logList) {
            try {
                String remark;
                if (RewardTypeEnum.COUPON.getKey().equals(log.getRewardType())) {
                    List<MtUserCoupon> userCoupons = mtUserCouponMapper.getUserCouponListByUuid(log.getCouponUuid());
                    boolean allInvalid = true;
                    if (userCoupons != null && userCoupons.size() > 0) {
                        for (MtUserCoupon userCoupon : userCoupons) {
                            if (UserCouponStatusEnum.UNUSED.getKey().equals(userCoupon.getStatus())) {
                                userCoupon.setStatus(UserCouponStatusEnum.DISABLE.getKey());
                                userCoupon.setOperator(operator);
                                userCoupon.setUpdateTime(new Date());
                                mtUserCouponMapper.updateById(userCoupon);
                            } else if (!UserCouponStatusEnum.DISABLE.getKey().equals(userCoupon.getStatus())
                                    && !UserCouponStatusEnum.EXPIRE.getKey().equals(userCoupon.getStatus())) {
                                allInvalid = false;
                            }
                        }
                    }
                    remark = allInvalid ? "退款追回：未使用券已作废（过期券仅记录）" : "退款追回：存在未作废的券，请人工核查";
                } else if (RewardTypeEnum.POINT.getKey().equals(log.getRewardType())) {
                    MtPoint mtPoint = new MtPoint();
                    mtPoint.setUserId(log.getUserId());
                    mtPoint.setAmount(-1 * (log.getPoint() == null ? 0 : log.getPoint()));
                    mtPoint.setDescription("退款追回推荐奖励积分");
                    mtPoint.setOperator(operator);
                    pointService.addPoint(mtPoint);
                    remark = "退款追回：积分已扣回";
                } else {
                    continue;
                }
                log.setClawbackStatus(2);
                log.setClawbackTime(new Date());
                log.setClawbackRemark(remark);
                log.setUpdateTime(new Date());
                mtCommissionLogMapper.updateById(log);
            } catch (Exception e) {
                logger.error("推荐奖励追回失败，logId = {}, 原因：{}", log.getId(), e.getMessage());
                log.setClawbackStatus(3);
                log.setClawbackTime(new Date());
                String remark = e.getMessage() == null ? "追回异常" : e.getMessage();
                log.setClawbackRemark(remark.length() > 200 ? remark.substring(0, 200) : remark);
                log.setUpdateTime(new Date());
                mtCommissionLogMapper.updateById(log);
            }
        }
    }

    /**
     * 会员端推荐奖励首页数据（活动规则 + 已邀请人数 + 已获券/积分）
     *
     * @param userId 会员ID
     * @return
     */
    @Override
    public Map<String, Object> getRewardSummary(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("rules", new ArrayList<>());
        result.put("inviteCount", 0);
        result.put("couponCount", 0);
        result.put("pointCount", 0);
        // 奖励发放开关关闭时不展示活动规则与邀请入口，仅展示历史统计
        result.put("rewardEnabled", rewardEnabled());

        MtUser user = memberService.queryMemberById(userId);
        if (user == null) {
            return result;
        }

        // 已邀请好友数
        Long inviteCount = mtCommissionRelationMapper.getInvitedUserCount(userId);
        result.put("inviteCount", inviteCount == null ? 0 : inviteCount.intValue());

        // 已获券/积分统计（已追回的不计入）
        List<MtCommissionLog> rewardLogs = queryUserRewardLogs(userId);
        int couponCount = 0;
        int pointCount = 0;
        for (MtCommissionLog log : rewardLogs) {
            if (CommissionStatusEnum.CLAWBACK.getKey().equals(log.getStatus())) {
                continue;
            }
            if (RewardTypeEnum.COUPON.getKey().equals(log.getRewardType())) {
                couponCount += log.getCouponNum() == null || log.getCouponNum() < 1 ? 1 : log.getCouponNum();
            } else if (RewardTypeEnum.POINT.getKey().equals(log.getRewardType())) {
                pointCount += log.getPoint() == null ? 0 : log.getPoint();
            }
        }
        result.put("couponCount", couponCount);
        result.put("pointCount", pointCount);

        // 启用中的推荐奖励方案 → 活动规则文案
        List<Map<String, Object>> ruleTexts = new ArrayList<>();
        List<MtCommissionRule> activeRules = queryActiveRecommendRules(user.getMerchantId(), user.getStoreId());
        for (MtCommissionRule rule : activeRules) {
            List<MtCommissionRuleItem> items = queryRuleItems(rule.getId());
            if ("Y".equals(rule.getRegisterSwitch())) {
                String text = buildRewardText(items, RewardSceneEnum.REGISTER.getKey());
                if (StringUtil.isNotEmpty(text)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("scene", "register");
                    item.put("text", "好友注册成功，你得 " + text);
                    ruleTexts.add(item);
                }
            }
            if ("Y".equals(rule.getOrderSwitch())) {
                String text = buildRewardText(items, RewardSceneEnum.ORDER.getKey());
                if (StringUtil.isNotEmpty(text)) {
                    BigDecimal threshold = rule.getOrderAmount() == null ? BigDecimal.ZERO : rule.getOrderAmount();
                    String prefix = threshold.compareTo(BigDecimal.ZERO) > 0
                            ? "好友单笔实付满 ¥" + threshold.stripTrailingZeros().toPlainString() + "，你再得 "
                            : "好友每笔实付订单，你再得 ";
                    Map<String, Object> item = new HashMap<>();
                    item.put("scene", "order");
                    item.put("text", prefix + text);
                    ruleTexts.add(item);
                }
            }
        }
        result.put("rules", ruleTexts);
        return result;
    }

    /**
     * 会员端推荐奖励记录分页（注册奖励 + 交易奖励）
     *
     * @param commissionLogPage 分页参数
     * @return
     */
    @Override
    public PaginationResponse<CommissionLogDto> queryRewardLogsByPagination(CommissionLogPage commissionLogPage) {
        commissionLogPage.setScene("reward");
        PaginationResponse<CommissionLogDto> response = queryCommissionLogByPagination(commissionLogPage);
        if (response.getContent() != null) {
            for (CommissionLogDto dto : response.getContent()) {
                // 来源：被推荐人昵称
                if (dto.getInviteeUserId() != null && dto.getInviteeUserId() > 0) {
                    MtUser invitee = memberService.queryMemberById(dto.getInviteeUserId());
                    if (invitee != null) {
                        dto.setInviteeName(invitee.getName());
                    }
                }
                // 来源：交易奖励关联订单号
                if (RewardSceneEnum.ORDER.getKey().equals(dto.getScene()) && dto.getOrderInfo() != null) {
                    dto.setOrderSn(dto.getOrderInfo().getOrderSn());
                }
                // 奖励内容：券名称
                if (RewardTypeEnum.COUPON.getKey().equals(dto.getRewardType()) && dto.getCouponId() != null) {
                    MtCoupon coupon = couponService.getById(dto.getCouponId());
                    if (coupon != null) {
                        dto.setCouponName(coupon.getName());
                    }
                }
            }
        }
        return response;
    }

    /**
     * 查询会员的全部推荐奖励记录（注册 + 交易，不含已删除）
     * */
    private List<MtCommissionLog> queryUserRewardLogs(Integer userId) {
        LambdaQueryWrapper<MtCommissionLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MtCommissionLog::getUserId, userId);
        wrapper.in(MtCommissionLog::getScene, Arrays.asList(RewardSceneEnum.REGISTER.getKey(), RewardSceneEnum.ORDER.getKey()));
        wrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());
        return mtCommissionLogMapper.selectList(wrapper);
    }

    /**
     * 按场景拼接奖励文案：券「xxx」×2 + 100积分
     * */
    private String buildRewardText(List<MtCommissionRuleItem> items, String scene) {
        List<String> texts = new ArrayList<>();
        for (MtCommissionRuleItem item : items) {
            if (!scene.equals(item.getScene())) {
                continue;
            }
            if (RewardTypeEnum.COUPON.getKey().equals(item.getRewardType())) {
                MtCoupon coupon = item.getCouponId() != null ? couponService.getById(item.getCouponId()) : null;
                String name = coupon != null ? coupon.getName() : "卡券";
                int num = item.getCouponNum() == null || item.getCouponNum() < 1 ? 1 : item.getCouponNum();
                texts.add("「" + name + "」×" + num);
            } else if (RewardTypeEnum.POINT.getKey().equals(item.getRewardType())) {
                texts.add((item.getPoint() == null ? 0 : item.getPoint()) + "积分");
            }
        }
        return String.join(" + ", texts);
    }

    /**
     * 推荐奖励开关（commission.reward.switch，默认开启，显式配置 0 关闭）
     * */
    private boolean rewardEnabled() {
        String theSwitch = environment.getProperty("commission.reward.switch");
        return theSwitch == null || !theSwitch.trim().equals("0");
    }

    /**
     * 方案是否在活动有效期内（未配置起止时间视为长期有效）
     *
     * @param mtCommissionRule 方案
     * @return
     */
    private boolean isRuleInPeriod(MtCommissionRule mtCommissionRule) {
        Date now = new Date();
        if (mtCommissionRule.getStartTime() != null && mtCommissionRule.getStartTime().after(now)) {
            return false;
        }
        if (mtCommissionRule.getEndTime() != null && mtCommissionRule.getEndTime().before(now)) {
            return false;
        }
        return true;
    }

    /**
     * 查询生效中的推荐奖励方案（启用中 + 活动时间内 + 店铺匹配）
     * */
    private List<MtCommissionRule> queryActiveRecommendRules(Integer merchantId, Integer storeId) {
        LambdaQueryWrapper<MtCommissionRule> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MtCommissionRule::getScene, "recommend");
        wrapper.eq(MtCommissionRule::getStatus, StatusEnum.ENABLED.getKey());
        wrapper.eq(MtCommissionRule::getMerchantId, merchantId);
        Date now = new Date();
        wrapper.and(w -> w.isNull(MtCommissionRule::getStartTime).or().le(MtCommissionRule::getStartTime, now));
        wrapper.and(w -> w.isNull(MtCommissionRule::getEndTime).or().ge(MtCommissionRule::getEndTime, now));
        List<MtCommissionRule> rules = mtCommissionRuleMapper.selectList(wrapper);
        List<MtCommissionRule> matched = new ArrayList<>();
        for (MtCommissionRule rule : rules) {
            if (StringUtil.isEmpty(rule.getStoreIds()) || storeId == null) {
                matched.add(rule);
                continue;
            }
            for (String storeIdStr : rule.getStoreIds().split(",")) {
                if (storeIdStr.trim().equals(String.valueOf(storeId))) {
                    matched.add(rule);
                    break;
                }
            }
        }
        return matched;
    }

    /**
     * 查询规则生效中的明细
     * */
    private List<MtCommissionRuleItem> queryRuleItems(Integer ruleId) {
        Map<String, Object> param = new HashMap<>();
        param.put("RULE_ID", ruleId);
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        return mtCommissionRuleItemMapper.selectByMap(param);
    }

    /**
     * 写奖励记录并立即发放；同规则项+同邀请人+同业务ID只发一次
     * */
    private void createAndGrantRewardLog(MtCommissionRule rule, MtCommissionRuleItem item, MtUser inviter, Integer inviteeUserId, Integer bizId) {
        LambdaQueryWrapper<MtCommissionLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MtCommissionLog::getRuleItemId, item.getId());
        wrapper.eq(MtCommissionLog::getUserId, inviter.getId());
        wrapper.eq(MtCommissionLog::getBizId, bizId);
        Long count = mtCommissionLogMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return;
        }
        MtCommissionLog log = new MtCommissionLog();
        log.setType(CommissionTypeEnum.RECOMMEND.getKey());
        log.setTarget(CommissionTargetEnum.MEMBER.getKey());
        log.setScene(item.getScene());
        log.setRewardType(item.getRewardType());
        log.setLevel(1);
        log.setUserId(inviter.getId());
        log.setInviteeUserId(inviteeUserId);
        log.setBizId(bizId);
        log.setOrderId(RewardSceneEnum.ORDER.getKey().equals(item.getScene()) ? bizId : 0);
        log.setMerchantId(rule.getMerchantId());
        log.setStoreId(rule.getStoreId() == null ? 0 : rule.getStoreId());
        log.setStaffId(0);
        log.setAmount(BigDecimal.ZERO);
        log.setRuleId(rule.getId());
        log.setRuleItemId(item.getId());
        log.setCashId(0);
        Date dateTime = new Date();
        log.setCreateTime(dateTime);
        log.setUpdateTime(dateTime);
        log.setStatus(CommissionStatusEnum.NORMAL.getKey());
        log.setClawbackStatus(0);
        log.setRetryCount(0);
        if (RewardTypeEnum.COUPON.getKey().equals(item.getRewardType())) {
            log.setCouponId(item.getCouponId());
            log.setCouponNum(item.getCouponNum() == null || item.getCouponNum() < 1 ? 1 : item.getCouponNum());
            log.setDescription(RewardSceneEnum.getName(item.getScene()) + "奖励：卡券");
        } else if (RewardTypeEnum.POINT.getKey().equals(item.getRewardType())) {
            log.setPoint(item.getPoint() == null ? 0 : item.getPoint());
            log.setDescription(RewardSceneEnum.getName(item.getScene()) + "奖励：积分");
        } else {
            return;
        }
        mtCommissionLogMapper.insert(log);
        grantRewardLog(log.getId());
    }

    /**
     * 查询订单关联的奖励记录（券/积分）
     * */
    private List<MtCommissionLog> queryOrderRewardLogs(Integer orderId) {
        LambdaQueryWrapper<MtCommissionLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MtCommissionLog::getOrderId, orderId);
        wrapper.eq(MtCommissionLog::getScene, RewardSceneEnum.ORDER.getKey());
        wrapper.in(MtCommissionLog::getRewardType, Arrays.asList(RewardTypeEnum.COUPON.getKey(), RewardTypeEnum.POINT.getKey()));
        wrapper.orderByAsc(MtCommissionLog::getId);
        return mtCommissionLogMapper.selectList(wrapper);
    }
}
