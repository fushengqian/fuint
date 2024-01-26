package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.CommissionLogDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.repository.mapper.MtCommissionLogMapper;
import com.fuint.repository.mapper.MtCommissionRuleItemMapper;
import com.fuint.repository.mapper.MtOrderGoodsMapper;
import com.fuint.repository.model.*;
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

    private MtCommissionRuleItemMapper mtCommissionRuleItemMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

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
     * 提成方案规则服务接口
     * */
    private CommissionRuleService commissionRuleService;

    /**
     * 分页查询记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(PaginationRequest paginationRequest) {
        Page<MtCommissionLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());

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

        lambdaQueryWrapper.orderByDesc(MtCommissionLog::getId);
        List<MtCommissionLog> commissionLogList = mtCommissionLogMapper.selectList(lambdaQueryWrapper);
        List<CommissionLogDto> dataList = new ArrayList<>();
        if (commissionLogList != null && commissionLogList.size() > 0) {
            for (MtCommissionLog mtCommissionLog : commissionLogList) {
                 CommissionLogDto commissionLogDto = new CommissionLogDto();
                 BeanUtils.copyProperties(mtCommissionLog, commissionLogDto);
                 commissionLogDto.setTypeName(OrderTypeEnum.getName(mtCommissionLog.getType()));
                 MtOrder mtOrder = orderService.getById(mtCommissionLog.getOrderId());
                 commissionLogDto.setOrderInfo(mtOrder);
                 MtStore mtStore = storeService.getById(mtCommissionLog.getStoreId());
                 commissionLogDto.setStoreInfo(mtStore);
                 MtStaff mtStaff = staffService.getById(mtCommissionLog.getStaffId());
                 commissionLogDto.setStaffInfo(mtStaff);
                 MtCommissionRule mtCommissionRule = commissionRuleService.getById(mtCommissionLog.getRuleId());
                 commissionLogDto.setRuleInfo(mtCommissionRule);
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
    public void calculateCommission(Integer orderId) {
        if (orderId != null && orderId > 0) {
            MtOrder mtOrder = orderService.getById(orderId);
            // 商品订单佣金计算
            if (mtOrder != null && mtOrder.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
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
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getType, OrderTypeEnum.GOOGS.getKey());
                         lambdaQueryWrapper.eq(MtCommissionRuleItem::getStatus, StatusEnum.ENABLED.getKey());
                         lambdaQueryWrapper.orderByDesc(MtCommissionRuleItem::getId);
                         List<MtCommissionRuleItem> commissionRuleItemList = mtCommissionRuleItemMapper.selectList(lambdaQueryWrapper);
                         if (commissionRuleItemList != null && commissionRuleItemList.size() > 0) {
                             MtCommissionRuleItem mtCommissionRuleItem = commissionRuleItemList.get(0);
                             MtCommissionLog mtCommissionLog = new MtCommissionLog();
                             BigDecimal amount = orderGoods.getPrice().multiply(mtCommissionRuleItem.getGuest().divide(new BigDecimal("100")));
                             mtCommissionLog.setType(mtOrder.getType());
                             mtCommissionLog.setLevel(0);
                             mtCommissionLog.setUserId(mtOrder.getUserId());
                             mtCommissionLog.setOrderId(orderId);
                             mtCommissionLog.setMerchantId(mtOrder.getMerchantId());
                             mtCommissionLog.setStoreId(mtOrder.getStoreId());
                             mtCommissionLog.setStaffId(mtOrder.getStaffId());
                             mtCommissionLog.setAmount(amount);
                             mtCommissionLog.setRuleId(mtCommissionRuleItem.getRuleId());
                             mtCommissionLog.setRuleItemId(mtCommissionRuleItem.getId());
                             mtCommissionLog.setCashId(0);
                             mtCommissionLog.setCashTime(null);
                             mtCommissionLog.setCreateTime(new Date());
                             mtCommissionLog.setUpdateTime(new Date());
                             mtCommissionLog.setStatus(StatusEnum.ENABLED.getKey());
                             mtCommissionLog.setOperator(null);
                             mtCommissionLogMapper.insert(mtCommissionLog);
                         }
                    }
                }
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
     * @param commissionLogRequest 请求参数
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新分销提成记录")
    public void updateCommissionLog(CommissionLogRequest commissionLogRequest) throws BusinessCheckException {
        MtCommissionLog mtCommissionLog =  mtCommissionLogMapper.selectById(commissionLogRequest.getId());
        if (mtCommissionLog == null) {
            logger.error("更新分销提成记录失败...");
            throw new BusinessCheckException("更新分销提成记录失败");
        }
        mtCommissionLog.setStatus(commissionLogRequest.getStatus() == null ? CommissionStatusEnum.NORMAL.getKey() : commissionLogRequest.getStatus());
        mtCommissionLog.setUpdateTime(new Date());
        if (commissionLogRequest.getAmount() != null) {
            mtCommissionLog.setAmount(new BigDecimal(commissionLogRequest.getAmount()));
        }
        if (commissionLogRequest.getDescription() != null) {
            mtCommissionLog.setDescription(commissionLogRequest.getDescription());
        }
        mtCommissionLog.setOperator(commissionLogRequest.getOperator());
        mtCommissionLogMapper.updateById(mtCommissionLog);
    }
}
