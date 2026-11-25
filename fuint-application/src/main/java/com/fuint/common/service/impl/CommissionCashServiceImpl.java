package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.commission.CommissionCashDto;
import com.fuint.common.dto.commission.CommissionOverviewDto;
import com.fuint.common.dto.order.OrderUserDto;
import com.fuint.common.enums.CommissionCashStatusEnum;
import com.fuint.common.enums.CommissionPayTypeEnum;
import com.fuint.common.enums.CommissionStatusEnum;
import com.fuint.common.enums.CommissionTargetEnum;
import com.fuint.common.enums.PlatformTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CommissionCashPage;
import com.fuint.common.param.WithdrawParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionCashRequest;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.module.backendApi.request.CommissionSettleConfirmRequest;
import com.fuint.module.backendApi.request.CommissionSettleRequest;
import com.fuint.repository.mapper.MtCommissionCashMapper;
import com.fuint.repository.mapper.MtCommissionLogMapper;
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
 * 分销提成提现服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
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
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 微信服务接口（商家转账付款到微信钱包）
     * */
    private WeixinService weixinService;

    /**
     * 分页查询提现列表
     *
     * @param commissionCashPage
     * @return
     */
    @Override
    public PaginationResponse<CommissionCashDto> queryCommissionCashByPagination(CommissionCashPage commissionCashPage) {
        Page<MtCommissionCash> pageHelper = PageHelper.startPage(commissionCashPage.getPage(), commissionCashPage.getPageSize());
        LambdaQueryWrapper<MtCommissionCash> lambdaQueryWrapper = Wrappers.lambdaQuery();
        String status = commissionCashPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getStatus, status);
        }
        Integer merchantId = commissionCashPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtCommissionCash::getMerchantId, merchantId);
        }
        Integer storeId = commissionCashPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtCommissionCash::getStoreId, storeId);
        }
        String realName = commissionCashPage.getRealName();
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
        String mobile = commissionCashPage.getMobile();
        if (StringUtils.isNotEmpty(mobile)) {
            MtStaff mtStaff = staffService.queryStaffByMobile(mobile);
            if (mtStaff != null) {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, mtStaff.getId());
            } else {
                lambdaQueryWrapper.eq(MtCommissionCash::getStaffId, -1);
            }
        }
        // 分佣对象类型：member会员；staff员工
        String userType = commissionCashPage.getUserType();
        if ("member".equals(userType)) {
            lambdaQueryWrapper.gt(MtCommissionCash::getUserId, 0);
        } else if ("staff".equals(userType)) {
            lambdaQueryWrapper.gt(MtCommissionCash::getStaffId, 0);
        }
        // 付款方式：A会员余额；B微信钱包
        String payType = commissionCashPage.getPayType();
        if (StringUtils.isNotBlank(payType)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getPayType, payType);
        }
        String uuid = commissionCashPage.getUuid();
        if (StringUtils.isNotBlank(uuid)) {
            lambdaQueryWrapper.eq(MtCommissionCash::getUuid, uuid);
        }
        // 开始时间、结束时间
        String startTime = commissionCashPage.getStartTime();
        String endTime = commissionCashPage.getEndTime();
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
                         // 兼容历史数据：会员提现时操作人曾存手机号/会员ID，统一显示会员名称
                         String operator = commissionCashDto.getOperator();
                         if (StringUtil.isNotEmpty(userDto.getName())
                                 && (StringUtil.isEmpty(operator) || operator.matches("\\d+"))) {
                             commissionCashDto.setOperator(userDto.getName());
                         }
                     }
                 }
                 if (mtStaff != null) {
                     mtStaff.setMobile(CommonUtil.hidePhone(mtStaff.getMobile()));
                     commissionCashDto.setStaffInfo(mtStaff);
                 }
                 dataList.add(commissionCashDto);
            }
        }
        PageRequest pageRequest = PageRequest.of(commissionCashPage.getPage(), commissionCashPage.getPageSize());
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
        // 结算只作用于佣金记录；推荐奖励（券/积分）即时到账，不参与结算
        lambdaQueryWrapper.eq(MtCommissionLog::getScene, "commission");
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
        String uuid = SeqUtil.getUUID();
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
            commissionCashDto = new CommissionCashDto();
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
        // 终态（已打款、已作废）记录不允许修改
        if (CommissionCashStatusEnum.PAYED.getKey().equals(mtCommissionCash.getStatus()) || CommissionCashStatusEnum.CANCEL.getKey().equals(mtCommissionCash.getStatus())) {
            throw new BusinessCheckException("该提现记录已打款或已作废，不允许修改");
        }
        mtCommissionCash.setUpdateTime(new Date());
        if (requestParam.getAmount() != null) {
            mtCommissionCash.setAmount(new BigDecimal(requestParam.getAmount()));
        }
        if (requestParam.getDescription() != null) {
            mtCommissionCash.setDescription(requestParam.getDescription());
        }
        if (requestParam.getStatus() != null) {
            // 校验状态取值合法性
            boolean validStatus = false;
            for (CommissionCashStatusEnum statusEnum : CommissionCashStatusEnum.values()) {
                if (statusEnum.getKey().equals(requestParam.getStatus())) {
                    validStatus = true;
                    break;
                }
            }
            if (!validStatus) {
                throw new BusinessCheckException("提现状态取值不合法");
            }
            // 校验状态流转：仅允许 待确认→已确认/已作废、已确认→已打款/已作废；状态不变视为仅修改其他字段
            String currentStatus = mtCommissionCash.getStatus();
            boolean sameStatus = currentStatus.equals(requestParam.getStatus());
            boolean allowedFlow =
                (CommissionCashStatusEnum.WAIT.getKey().equals(currentStatus)
                    && (CommissionCashStatusEnum.SETTLED.getKey().equals(requestParam.getStatus()) || CommissionCashStatusEnum.CANCEL.getKey().equals(requestParam.getStatus())))
                || (CommissionCashStatusEnum.SETTLED.getKey().equals(currentStatus)
                    && (CommissionCashStatusEnum.PAYED.getKey().equals(requestParam.getStatus()) || CommissionCashStatusEnum.CANCEL.getKey().equals(requestParam.getStatus())));
            if (!sameStatus && !allowedFlow) {
                throw new BusinessCheckException("提现状态不允许这样变更，请通过确认结算、取消结算或付款到余额接口操作");
            }
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
            throw new BusinessCheckException("请求参数有误");
        }
        boolean flag = mtCommissionCashMapper.cancelCommissionCash(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
        if (flag) {
            mtCommissionLogMapper.cancelCommissionLog(requestParam.getMerchantId(), requestParam.getUuid(), requestParam.getOperator());
        }
    }

    /**
     * 支付结算金额到用户余额
     *
     * @param commissionCashRequest 请求参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "支付结算金额到用户余额")
    public void payToBalance(CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        MtCommissionCash mtCommissionCash = mtCommissionCashMapper.selectById(commissionCashRequest.getId());
        if (mtCommissionCash == null) {
            throw new BusinessCheckException("请求参数有误");
        }
        if (mtCommissionCash.getStatus().equals(CommissionCashStatusEnum.PAYED.getKey())) {
            throw new BusinessCheckException("该记录已经支付过了");
        }
        if (mtCommissionCash.getStatus().equals(CommissionCashStatusEnum.CANCEL.getKey())) {
            throw new BusinessCheckException("该记录已经作废了");
        }
        BigDecimal amount = mtCommissionCash.getAmount();
        if (StringUtil.isNotBlank(commissionCashRequest.getAmount()) && (new BigDecimal(commissionCashRequest.getAmount()).compareTo(new BigDecimal("0")) > 0)) {
            if (new BigDecimal(commissionCashRequest.getAmount()).compareTo(amount) > 0) {
                throw new BusinessCheckException("付款金额不能大于" + amount);
            }
            amount = new BigDecimal(commissionCashRequest.getAmount());
        }
        mtCommissionCash.setOperator(commissionCashRequest.getOperator());
        mtCommissionCash.setStatus(CommissionCashStatusEnum.PAYED.getKey());
        mtCommissionCash.setPayType(CommissionPayTypeEnum.BALANCE.getKey());
        mtCommissionCash.setPayTime(new Date());
        mtCommissionCash.setAmount(amount);
        mtCommissionCash.setUpdateTime(new Date());
        mtCommissionCash.setDescription(commissionCashRequest.getDescription());
        Integer i = mtCommissionCashMapper.updateById(mtCommissionCash);
        if (i > 0 && mtCommissionCash.getUserId() != null) {
            MtUser mtUser = memberService.queryMemberById(mtCommissionCash.getUserId());
            if (mtCommissionCash.getStaffId() != null && mtCommissionCash.getStaffId() > 0) {
                MtStaff mtStaff = staffService.queryStaffById(mtCommissionCash.getStaffId());
                mtUser = memberService.queryMemberByMobile(mtCommissionCash.getMerchantId(), mtStaff.getMobile());
            }
            if (mtUser != null) {
                MtBalance mtBalance = new MtBalance();
                mtBalance.setMerchantId(mtCommissionCash.getMerchantId());
                mtBalance.setStoreId(mtCommissionCash.getStoreId());
                mtBalance.setUserId(mtUser.getId());
                mtBalance.setAmount(amount);
                mtBalance.setStatus(StatusEnum.ENABLED.getKey());
                mtBalance.setMobile(mtUser.getMobile());
                mtBalance.setDescription("发放分享佣金");
                balanceService.addBalance(mtBalance, true);
            } else {
                throw new BusinessCheckException("付款失败，未找到会员信息");
            }
        }
    }

    /**
     * 会员端申请提现（生成待审核记录，由管理员审核后付款）
     *
     * @param  withdrawParam 请求参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "会员申请提现")
    public Boolean withdraw(WithdrawParam withdrawParam) throws BusinessCheckException {
        MtUser mtUser = memberService.queryMemberById(withdrawParam.getUserId());
        if (mtUser == null) {
            throw new BusinessCheckException("会员不存在");
        }
        // 存在待审核或审核通过的申请时不重复生成，刷新已有申请的操作信息
        List<MtCommissionCash> processingList = mtCommissionCashMapper.selectList(Wrappers.<MtCommissionCash>lambdaQuery()
                .eq(MtCommissionCash::getUserId, withdrawParam.getUserId())
                .in(MtCommissionCash::getStatus, Arrays.asList(
                        CommissionCashStatusEnum.WAIT.getKey(), CommissionCashStatusEnum.SETTLED.getKey()))
                .orderByDesc(MtCommissionCash::getId));
        if (processingList != null && !processingList.isEmpty()) {
            MtCommissionCash updateCash = new MtCommissionCash();
            updateCash.setId(processingList.get(0).getId());
            Date now = new Date();
            updateCash.setCreateTime(now);
            updateCash.setUpdateTime(now);
            updateCash.setOperator(StringUtil.isNotEmpty(mtUser.getName()) ? mtUser.getName()
                    : (StringUtil.isNotEmpty(mtUser.getMobile()) ? mtUser.getMobile() : String.valueOf(mtUser.getId())));
            mtCommissionCashMapper.updateById(updateCash);
            return false;
        }
        // 提现金额固定为全部可提现金额（总佣金 - 已打款），不支持自定义金额
        CommissionOverviewDto overview = commissionLogService.getCommissionOverview(withdrawParam.getUserId());
        BigDecimal withdrawable = overview.getAmount() == null ? BigDecimal.ZERO : overview.getAmount();
        if (withdrawable.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessCheckException("暂无可提现金额");
        }
        Date time = new Date();
        MtCommissionCash mtCommissionCash = new MtCommissionCash();
        mtCommissionCash.setSettleNo(CommonUtil.createSettlementNo());
        mtCommissionCash.setUuid(SeqUtil.getUUID());
        mtCommissionCash.setMerchantId(mtUser.getMerchantId());
        mtCommissionCash.setStoreId(mtUser.getStoreId());
        mtCommissionCash.setUserId(withdrawParam.getUserId());
        mtCommissionCash.setStaffId(0);
        mtCommissionCash.setAmount(withdrawable);
        mtCommissionCash.setDescription(StringUtil.isNotEmpty(withdrawParam.getRemark()) ? withdrawParam.getRemark() : "会员申请提现");
        mtCommissionCash.setCreateTime(time);
        mtCommissionCash.setUpdateTime(time);
        // 会员提现由会员本人发起，操作人记录会员名称
        mtCommissionCash.setOperator(StringUtil.isNotEmpty(mtUser.getName()) ? mtUser.getName()
                : (StringUtil.isNotEmpty(mtUser.getMobile()) ? mtUser.getMobile() : String.valueOf(mtUser.getId())));
        mtCommissionCash.setStatus(CommissionCashStatusEnum.WAIT.getKey());
        return this.save(mtCommissionCash);
    }

    /**
     * 商家转账付款到微信钱包
     *
     * @param commissionCashRequest 请求参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "付款到微信钱包")
    public void payToWechat(CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        MtCommissionCash mtCommissionCash = mtCommissionCashMapper.selectById(commissionCashRequest.getId());
        if (mtCommissionCash == null) {
            throw new BusinessCheckException("请求参数有误");
        }
        if (mtCommissionCash.getStatus().equals(CommissionCashStatusEnum.PAYED.getKey())) {
            throw new BusinessCheckException("该记录已经支付过了");
        }
        if (mtCommissionCash.getStatus().equals(CommissionCashStatusEnum.CANCEL.getKey())) {
            throw new BusinessCheckException("该记录已经作废了");
        }
        BigDecimal amount = mtCommissionCash.getAmount();
        if (StringUtil.isNotBlank(commissionCashRequest.getAmount()) && (new BigDecimal(commissionCashRequest.getAmount()).compareTo(new BigDecimal("0")) > 0)) {
            if (new BigDecimal(commissionCashRequest.getAmount()).compareTo(amount) > 0) {
                throw new BusinessCheckException("付款金额不能大于" + amount);
            }
            amount = new BigDecimal(commissionCashRequest.getAmount());
        }
        // 收款会员：会员结算直接取会员；员工结算按员工手机号找对应会员
        MtUser mtUser = null;
        if (mtCommissionCash.getUserId() != null && mtCommissionCash.getUserId() > 0) {
            mtUser = memberService.queryMemberById(mtCommissionCash.getUserId());
        }
        if (mtUser == null && mtCommissionCash.getStaffId() != null && mtCommissionCash.getStaffId() > 0) {
            MtStaff mtStaff = staffService.queryStaffById(mtCommissionCash.getStaffId());
            if (mtStaff != null) {
                mtUser = memberService.queryMemberByMobile(mtCommissionCash.getMerchantId(), mtStaff.getMobile());
            }
        }
        if (mtUser == null || StringUtil.isEmpty(mtUser.getOpenId())) {
            throw new BusinessCheckException("该会员未绑定微信，无法转账到微信钱包");
        }
        // 调起微信商家转账（APIv3）
        String outBatchNo = "WT" + System.currentTimeMillis() + SeqUtil.getRandomNumber(4);
        String batchRemark = StringUtil.isNotBlank(commissionCashRequest.getDescription()) ? commissionCashRequest.getDescription() : "佣金提现";
        String batchId = weixinService.transferToUser(mtCommissionCash.getStoreId(), PlatformTypeEnum.MP_WEIXIN.getCode(),
                mtUser.getOpenId(), outBatchNo, amount, "佣金提现", batchRemark);
        // 受理成功即标记已打款，批次单号落库供对账
        Date time = new Date();
        mtCommissionCash.setOperator(commissionCashRequest.getOperator());
        mtCommissionCash.setStatus(CommissionCashStatusEnum.PAYED.getKey());
        mtCommissionCash.setPayType(CommissionPayTypeEnum.WECHAT.getKey());
        mtCommissionCash.setPayTime(time);
        mtCommissionCash.setTransferNo(StringUtil.isNotEmpty(batchId) ? batchId : outBatchNo);
        mtCommissionCash.setAmount(amount);
        mtCommissionCash.setUpdateTime(time);
        mtCommissionCash.setDescription(commissionCashRequest.getDescription());
        mtCommissionCashMapper.updateById(mtCommissionCash);
    }
}
