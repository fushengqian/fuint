package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.CommissionLogDto;
import com.fuint.common.dto.OrderUserDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * 分销提成记录服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
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
     * 分页查询分销提成列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());
        String target = paginationRequest.getSearchParams().get("target") == null ? "" : paginationRequest.getSearchParams().get("target").toString();
        if (StringUtils.isNotBlank(target)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getTarget, target);
        }
        String realName = paginationRequest.getSearchParams().get("realName") == null ? "" : paginationRequest.getSearchParams().get("realName").toString();
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
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            MtStaff mtStaff = staffService.queryStaffByMobile(mobile);
            if (mtStaff != null) {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, mtStaff.getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, -1);
            }
        }
        String uuid = paginationRequest.getSearchParams().get("uuid") == null ? "" : paginationRequest.getSearchParams().get("uuid").toString();
        if (StringUtils.isNotBlank(uuid)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getSettleUuid, uuid);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStoreId, storeId);
        }
        // 开始时间、结束时间
        String startTime = paginationRequest.getSearchParams().get("startTime") == null ? "" : paginationRequest.getSearchParams().get("startTime").toString();
        String endTime = paginationRequest.getSearchParams().get("endTime") == null ? "" : paginationRequest.getSearchParams().get("endTime").toString();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtCommissionLog::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtCommissionLog::getCreateTime, endTime);
        }

        lambdaQueryWrapper.orderByDesc(MtCommissionLog::getId);
        Page<MtCommissionLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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
                 commissionLogDto.setStaffInfo(mtStaff);
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
                         userDto.setMobile(userInfo.getMobile());
                         commissionLogDto.setUserInfo(userDto);
                     }
                 }
                 dataList.add(commissionLogDto);
            }
        }
        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<CommissionLogDto> paginationResponse = new PaginationResponse(pageImpl, CommissionLogDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
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
            // 商品订单佣金计算
            if (mtOrder != null && mtOrder.getType().equals(CommissionTypeEnum.GOODS.getKey())) {
                // 获取分销关系
                Integer commissionUserId = mtCommissionRelationMapper.getCommissionUserId(mtOrder.getMerchantId(), mtOrder.getUserId());
                if (commissionUserId != null && commissionUserId > 0) {
                    mtOrder.setCommissionUserId(commissionUserId);
                    orderService.updateOrder(mtOrder);
                }
                Map<String, Object> params = new HashMap<>();
                params.put("ORDER_ID", mtOrder.getId());
                params.put("STATUS", StatusEnum.ENABLED.getKey());
                List<MtOrderGoods> goodsList = mtOrderGoodsMapper.selectByMap(params);
                if (goodsList != null && goodsList.size() > 0) {
                    for (MtOrderGoods orderGoods : goodsList) {
                         Integer goodsId = orderGoods.getGoodsId();
                         LambdaQueryWrapper<MtCommissionRuleItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getMerchantId, mtOrder.getMerchantId());
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getTargetId, goodsId);
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getType, CommissionTypeEnum.GOODS.getKey());
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getStatus, StatusEnum.ENABLED.getKey());
                         lambdaQueryWrapper.orderByDesc(MtCommissionRuleItem::getId);
                         List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.selectList(lambdaQueryWrapper);
                         if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                             MtCommissionRuleItem mtCommissionRuleItem = commissionRuleItemList.get(0);
                             MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(mtCommissionRuleItem.getRuleId());
                             // 规则状态正常
                             if (mtCommissionRule != null && mtCommissionRule.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                                 // 分佣金额计算，散客和会员佣金比例不同
                                 BigDecimal rate = mtCommissionRuleItem.getMember();
                                 if (mtOrder.getStaffId() != null && mtOrder.getStaffId() > 0 && mtOrder.getIsVisitor().equals(YesOrNoEnum.YES.getKey())) {
                                     rate = mtCommissionRuleItem.getGuest();
                                 }
                                 BigDecimal amount = orderGoods.getPrice().multiply(rate.divide(new BigDecimal("100")));

                                 // 员工提成计算，员工信息不能为空
                                 if (mtOrder.getStaffId() > 0 && mtCommissionRule.getTarget().equals(CommissionTargetEnum.STAFF.getKey())) {
                                     if (mtOrder.getStaffId() != null && mtOrder.getStaffId() > 0) {
                                         addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, 0);
                                     }
                                 }

                                 // 会员分销计算，会员信息不能为空
                                 if (mtOrder.getCommissionUserId() > 0 && mtCommissionRule.getTarget().equals(CommissionTargetEnum.MEMBER.getKey())) {
                                     addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, mtOrder.getCommissionUserId());
                                 }
                             }
                         }
                    }
                }
            }

            // 充值订单计算佣金
            if (mtOrder != null && mtOrder.getType().equals(CommissionTypeEnum.RECHARGE.getKey())) {
                LambdaQueryWrapper<MtCommissionRuleItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(MtCommissionRuleItem::getMerchantId, mtOrder.getMerchantId());
                lambdaQueryWrapper.eq(MtCommissionRuleItem::getType, CommissionTypeEnum.RECHARGE.getKey());
                lambdaQueryWrapper.eq(MtCommissionRuleItem::getStatus, StatusEnum.ENABLED.getKey());
                lambdaQueryWrapper.orderByDesc(MtCommissionRuleItem::getId);
                List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.selectList(lambdaQueryWrapper);
                if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                    MtCommissionRuleItem mtCommissionRuleItem = commissionRuleItemList.get(0);
                    MtCommissionRule mtCommissionRule = mtCommissionRuleMapper.selectById(mtCommissionRuleItem.getRuleId());

                    // 分佣金额计算，散客和会员佣金比例不同
                    BigDecimal rate = mtCommissionRuleItem.getMember();
                    if (mtOrder.getStaffId() != null && mtOrder.getStaffId() > 0 && mtOrder.getIsVisitor().equals(YesOrNoEnum.YES.getKey())) {
                        rate = mtCommissionRuleItem.getGuest();
                    }
                    BigDecimal amount = mtOrder.getAmount().multiply(rate.divide(new BigDecimal("100")));
                    addCommissionLog(mtOrder, mtCommissionRule, amount, mtCommissionRuleItem, 0);
                }
            }

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
     * @return
     * */
    @Transactional
    @OperationServiceLog(description = "新增分销提成记录")
    public void addCommissionLog(MtOrder mtOrder, MtCommissionRule mtCommissionRule, BigDecimal amount, MtCommissionRuleItem mtCommissionRuleItem, Integer userId) {
        MtCommissionLog mtCommissionLog = new MtCommissionLog();
        mtCommissionLog.setType(mtOrder.getType());
        mtCommissionLog.setTarget(mtCommissionRule.getTarget());
        mtCommissionLog.setLevel(0);
        mtCommissionLog.setUserId(userId);
        mtCommissionLog.setOrderId(mtOrder.getId());
        mtCommissionLog.setMerchantId(mtOrder.getMerchantId());
        mtCommissionLog.setStoreId(mtOrder.getStoreId());
        mtCommissionLog.setStaffId(mtOrder.getStaffId());
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
