package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.OrderUserDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionCashRequest;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.module.backendApi.request.CommissionSettleConfirmRequest;
import com.fuint.module.backendApi.request.CommissionSettleRequest;
import com.fuint.repository.mapper.MtCommissionCashMapper;
import com.fuint.common.dto.CommissionCashDto;
import com.fuint.repository.mapper.MtCommissionLogMapper;
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
 * 分销提成提现服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class CommissionCashServiceImpl extends ServiceImpl<MtCommissionCashMapper, MtCommissionCash> implements CommissionCashService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionCashServiceImpl.class);

    private MtCommissionCashMapper mtCommissionCashMapper;

    private MtCommissionLogMapper mtCommissionLogMapper;

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
     * 分销提成记录业务接口
     */
    private CommissionLogService commissionLogService;

    /**
     * 分页查询提现列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<CommissionCashDto> queryCommissionCashByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtCommissionCash> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionCash> lambdaQueryWrapper = Wrappers.lambdaQuery();
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getStoreId, storeId);
        }
        String realName = paginationRequest.getSearchParams().get("realName") == null ? "" : paginationRequest.getSearchParams().get("realName").toString();
        if (StringUtils.isNotEmpty(realName)) {
            Map<String, Object> params = new HashMap<>();
            params.put("REAL_NAME", realName);
            params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
            List<MtStaff> staffList = staffService.queryStaffByParams(params);
            if (staffList != null && staffList.size() > 0) {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, staffList.get(0).getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, -1);
            }
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotEmpty(mobile)) {
            MtStaff mtStaff = staffService.queryStaffByMobile(mobile);
            if (mtStaff != null) {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, mtStaff.getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, -1);
            }
        }
        String uuid = paginationRequest.getSearchParams().get("uuid") == null ? "" : paginationRequest.getSearchParams().get("uuid").toString();
        if (StringUtils.isNotBlank(uuid)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getUuid, uuid);
        }
        // 开始时间、结束时间
        String startTime = paginationRequest.getSearchParams().get("startTime") == null ? "" : paginationRequest.getSearchParams().get("startTime").toString();
        String endTime = paginationRequest.getSearchParams().get("endTime") == null ? "" : paginationRequest.getSearchParams().get("endTime").toString();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtCommissionCash::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtCommissionCash::getCreateTime, endTime);
        }
        lambdaQueryWrapper.orderByDesc(MtCommissionCash::getId);
        List<MtCommissionCash> commissionCashList = mtCommissionCashMapper.selectList(lambdaQueryWrapper);
        List<CommissionCashDto> dataList = new ArrayList<>();
        if (commissionCashList != null && commissionCashList.size() > 0) {
            for (MtCommissionCash mtCommissionCash : commissionCashList) {
                 CommissionCashDto commissionCashDto = new CommissionCashDto();
                 BeanUtils.copyProperties(mtCommissionCash, commissionCashDto);
                 MtStore mtStore = storeService.getById(mtCommissionCash.getStoreId());
                 commissionCashDto.setStoreInfo(mtStore);
                 MtStaff mtStaff = staffService.getById(mtCommissionCash.getStaffId());
                 if (mtCommissionCash.getUserId() != null && mtCommissionCash.getUserId() > 0) {
                     MtUser userInfo = memberService.queryMemberById(mtCommissionCash.getUserId());
                     if (userInfo != null) {
                         OrderUserDto userDto = new OrderUserDto();
                         userDto.setNo(userInfo.getUserNo());
                         userDto.setId(userInfo.getId());
                         userDto.setName(userInfo.getName());
                         userDto.setCardNo(userInfo.getIdcard());
                         userDto.setAddress(userInfo.getAddress());
                         userDto.setMobile(CommonUtil.hidePhone(userInfo.getMobile()));
                         commissionCashDto.setUserInfo(userDto);
                     }
                 }
                 if (mtStaff != null) {
                     mtStaff.setMobile(CommonUtil.hidePhone(mtStaff.getMobile()));
                     commissionCashDto.setStaffInfo(mtStaff);
                 }
                 dataList.add(commissionCashDto);
            }
        }
        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<CommissionCashDto> paginationResponse = new PaginationResponse(pageImpl, CommissionCashDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 分销提成结算
     *
     * @param commissionSettleRequest 结算参数
     * @return
     */
    @Override
    @Transactional
    public String settleCommission(CommissionSettleRequest commissionSettleRequest) throws BusinessCheckException {
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtCommissionLog::getStatus, CommissionStatusEnum.NORMAL.getKey());
        if (commissionSettleRequest.getMerchantId() != null && StringUtils.isNotBlank(commissionSettleRequest.getMerchantId().toString())) {
            lambdaQueryWrapper.eq(MtCommissionLog::getMerchantId, commissionSettleRequest.getMerchantId());
        }
        if (commissionSettleRequest.getStoreId() != null && StringUtils.isNotBlank(commissionSettleRequest.getStoreId().toString())) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStoreId, commissionSettleRequest.getStoreId());
        }
        String realName = commissionSettleRequest.getRealName();
        if (StringUtils.isNotBlank(realName)) {
            Map<String, Object> params = new HashMap<>();
            params.put("REAL_NAME", realName);
            params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
            List<MtStaff> staffList = staffService.queryStaffByParams(params);
            if (staffList != null && staffList.size() > 0) {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, staffList.get(0).getId());
            }
        }
        String mobile = commissionSettleRequest.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            MtStaff mtStaff = staffService.queryStaffByMobile(mobile);
            if (mtStaff != null) {
                lambdaQueryWrapper.eq(MtCommissionLog::getStaffId, mtStaff.getId());
            }
        }
        lambdaQueryWrapper.orderByDesc(MtCommissionLog::getId);
        List<MtCommissionLog> commissionLogList = mtCommissionLogMapper.selectList(lambdaQueryWrapper);
        List<String> staffIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        if (commissionLogList != null && commissionLogList.size() > 0) {
            for (MtCommissionLog mtCommissionLog : commissionLogList) {
                 if (mtCommissionLog.getStaffId() != null && mtCommissionLog.getStaffId() > 0 && !staffIds.contains(CommissionTargetEnum.STAFF.getKey() + mtCommissionLog.getStaffId())) {
                     staffIds.add(CommissionTargetEnum.STAFF.getKey() + mtCommissionLog.getStaffId());
                 } else if (mtCommissionLog.getUserId() != null && mtCommissionLog.getUserId() > 0 && !userIds.contains(CommissionTargetEnum.MEMBER.getKey() + mtCommissionLog.getUserId())){
                     userIds.add(CommissionTargetEnum.MEMBER.getKey() + mtCommissionLog.getUserId());
                }
            }
        }
        staffIds.addAll(userIds);
        // 生成结算数据
        if (staffIds.size() > 0) {
            for (String staffId : staffIds) {
                 BigDecimal totalAmount = new BigDecimal("0");
                 Integer cashMerchantId = 0;
                 Integer cashStoreId = 0;
                 String settleNo = CommonUtil.createSettlementNo();
                 Integer targetId;
                 if (staffId.indexOf(CommissionTargetEnum.STAFF.getKey()) >= 0) {
                     targetId = Integer.parseInt(staffId.replaceAll(CommissionTargetEnum.STAFF.getKey(), ""));
                 } else {
                     targetId = Integer.parseInt(staffId.replaceAll(CommissionTargetEnum.MEMBER.getKey(), ""));
                 }
                 for (MtCommissionLog mtCommissionLog : commissionLogList) {
                      if (mtCommissionLog.getStaffId().equals(targetId) || mtCommissionLog.getUserId().equals(targetId)) {
                          if (mtCommissionLog.getType().equals(CommissionTargetEnum.STAFF.getKey()) && staffId.indexOf(CommissionTargetEnum.STAFF.getKey()) < 0) {
                              continue;
                          }
                          if (mtCommissionLog.getType().equals(CommissionTargetEnum.MEMBER.getKey()) && staffId.indexOf(CommissionTargetEnum.MEMBER.getKey()) < 0) {
                              continue;
                          }
                          totalAmount = totalAmount.add(mtCommissionLog.getAmount());
                          if (mtCommissionLog.getMerchantId() != null && mtCommissionLog.getMerchantId() > 0) {
                              cashMerchantId = mtCommissionLog.getMerchantId();
                          }
                          if (mtCommissionLog.getStoreId() != null && mtCommissionLog.getStoreId() > 0) {
                              cashStoreId = mtCommissionLog.getStoreId();
                          }
                          CommissionLogRequest commissionLogRequest = new CommissionLogRequest();
                          commissionLogRequest.setId(mtCommissionLog.getId());
                          commissionLogRequest.setSettleUuid(uuid);
                          commissionLogRequest.setOperator(commissionSettleRequest.getOperator());
                          commissionLogRequest.setStatus(CommissionStatusEnum.SETTLED.getKey());
                          commissionLogService.updateCommissionLog(commissionLogRequest);
                     }
                 }
                 MtCommissionCash mtCommissionCash = new MtCommissionCash();
                 mtCommissionCash.setSettleNo(settleNo);
                 mtCommissionCash.setUuid(uuid);
                 if (staffId.indexOf(CommissionTargetEnum.STAFF.getKey()) >= 0) {
                     mtCommissionCash.setStaffId(targetId);
                     mtCommissionCash.setUserId(0);
                 } else {
                     mtCommissionCash.setUserId(targetId);
                     mtCommissionCash.setStaffId(0);
                 }
                 if (cashStoreId > 0) {
                     mtCommissionCash.setStoreId(cashStoreId);
                 }
                 if (cashMerchantId > 0) {
                     mtCommissionCash.setMerchantId(cashMerchantId);
                 }
                 mtCommissionCash.setAmount(totalAmount);
                 Date time = new Date();
                 mtCommissionCash.setCreateTime(time);
                 mtCommissionCash.setUpdateTime(time);
                 mtCommissionCash.setOperator(commissionSettleRequest.getOperator());
                 mtCommissionCash.setStatus(CommissionCashStatusEnum.WAIT.getKey());
                 this.save(mtCommissionCash);
            }
        }
        return uuid;
    }

    /**
     * 根据ID获取记录信息
     *
     * @param id 分佣提成提现ID
     * @return
     */
    @Override
    public CommissionCashDto queryCommissionCashById(Integer id) {
        MtCommissionCash mtCommissionCash = mtCommissionCashMapper.selectById(id);
        CommissionCashDto commissionCashDto = null;
        if (mtCommissionCash != null) {
            BeanUtils.copyProperties(mtCommissionCash, commissionCashDto);
        }
        return commissionCashDto;
    }

    /**
     * 更新分销提成提现
     *
     * @param requestParam 请求参数
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新分销提成提现")
    public void updateCommissionCash(CommissionCashRequest requestParam) throws BusinessCheckException {
        MtCommissionCash mtCommissionCash =  mtCommissionCashMapper.selectById(requestParam.getId());
        if (mtCommissionCash == null) {
            logger.error("更新分销提成提现失败...");
            throw new BusinessCheckException("更新分销提成提现失败，数据不存在");
        }
        mtCommissionCash.setUpdateTime(new Date());
        if (requestParam.getAmount() != null) {
            mtCommissionCash.setAmount(new BigDecimal(requestParam.getAmount()));
        }
        if (requestParam.getDescription() != null) {
            mtCommissionCash.setDescription(requestParam.getDescription());
        }
        if (requestParam.getStatus() != null) {
            mtCommissionCash.setStatus(requestParam.getStatus());
        }
        mtCommissionCash.setOperator(requestParam.getOperator());
        mtCommissionCashMapper.updateById(mtCommissionCash);
    }

    /**
     * 结算确认
     *
     * @param  requestParam 确认参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "结算确认")
    public void confirmCommissionCash(CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
       if (StringUtil.isEmpty(requestParam.getUuid())) {
           throw new BusinessCheckException("请求有误.");
       }
       boolean flag = mtCommissionCashMapper.confirmCommissionCash(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
       if (flag) {
           mtCommissionLogMapper.confirmCommissionLog(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
       }
    }

    /**
     * 结算确认
     *
     * @param requestParam 取消参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "取消结算")
    public void cancelCommissionCash(CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        if (StringUtil.isEmpty(requestParam.getUuid())) {
            throw new BusinessCheckException("请求有误.");
        }
        boolean flag = mtCommissionCashMapper.cancelCommissionCash(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
        if (flag) {
            mtCommissionLogMapper.cancelCommissionLog(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
        }
    }
}
