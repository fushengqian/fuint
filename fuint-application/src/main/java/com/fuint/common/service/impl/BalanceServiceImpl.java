package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.param.BalancePage;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.PhoneFormatCheckUtils;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBalanceMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtBalance;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 余额管理业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class BalanceServiceImpl extends ServiceImpl<MtBalanceMapper, MtBalance> implements BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceServiceImpl.class);

    private MtBalanceMapper mtBalanceMapper;

    private MtUserMapper mtUserMapper;

    /**
     * 微信相关服务接口
     * */
    private WeixinService weixinService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 短信发送服务接口
     * */
    private SendSmsService sendSmsService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 分页查询余额列表
     *
     * @param balancePage
     * @return
     */
    @Override
    public PaginationResponse<BalanceDto> queryBalanceListByPagination(BalancePage balancePage) throws BusinessCheckException {
        LambdaQueryWrapper<MtBalance> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBalance::getStatus, StatusEnum.DISABLE.getKey());

        String description = balancePage.getDescription();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtBalance::getDescription, description);
        }
        String status = balancePage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBalance::getStatus, status);
        }
        Integer userId = balancePage.getUserId();
        if (userId != null) {
            lambdaQueryWrapper.eq(MtBalance::getUserId, userId);
        }
        String orderSn = balancePage.getOrderSn();
        if (StringUtils.isNotBlank(orderSn)) {
            lambdaQueryWrapper.eq(MtBalance::getOrderSn, orderSn);
        }
        String mobile = balancePage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtBalance::getMobile, mobile);
        }
        Integer merchantId = balancePage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtBalance::getMerchantId, merchantId);
        }
        String userNo = balancePage.getUserNo();
        if (StringUtil.isNotEmpty(userNo)) {
            if (merchantId == null) {
                merchantId = 0;
            }
            MtUser userInfo = memberService.queryMemberByUserNo(merchantId, userNo);
            if (userInfo != null) {
                lambdaQueryWrapper.eq(MtBalance::getUserId, userInfo.getId());
            } else {
                lambdaQueryWrapper.eq(MtBalance::getUserId, -1);
            }
        }
        Integer storeId = balancePage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtBalance::getStoreId, storeId);
        }
        lambdaQueryWrapper.orderByDesc(MtBalance::getId);
        Page<MtBanner> pageHelper = PageHelper.startPage(balancePage.getPage(), balancePage.getPageSize());
        List<MtBalance> balanceList = mtBalanceMapper.selectList(lambdaQueryWrapper);

        List<BalanceDto> dataList = new ArrayList<>();
        for (MtBalance mtBalance : balanceList) {
            MtUser userInfo = memberService.queryMemberById(mtBalance.getUserId());
            if (userInfo != null) {
                userInfo.setMobile(CommonUtil.hidePhone(userInfo.getMobile()));
            }
            BalanceDto item = new BalanceDto();
            item.setId(mtBalance.getId());
            item.setAmount(mtBalance.getAmount());
            item.setDescription(mtBalance.getDescription());
            item.setCreateTime(mtBalance.getCreateTime());
            item.setUpdateTime(mtBalance.getUpdateTime());
            item.setUserId(mtBalance.getUserId());
            item.setUserInfo(userInfo);
            item.setOrderSn(mtBalance.getOrderSn());
            item.setStatus(mtBalance.getStatus());
            item.setOperator(mtBalance.getOperator());
            dataList.add(item);
        }

        PageRequest pageRequest = PageRequest.of(balancePage.getPage(), balancePage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<BalanceDto> paginationResponse = new PaginationResponse(pageImpl, BalanceDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加余额记录
     *
     * @param  mtBalance
     * @param  updateBalance
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "会员余额变动")
    public Boolean addBalance(MtBalance mtBalance, Boolean updateBalance) throws BusinessCheckException {
        if (mtBalance.getUserId() < 0) {
            return false;
        }
        Date nowDate = new Date();
        mtBalance.setStatus(StatusEnum.ENABLED.getKey());
        mtBalance.setCreateTime(nowDate);
        mtBalance.setUpdateTime(nowDate);

        MtUser mtUser = mtUserMapper.selectById(mtBalance.getUserId());
        BigDecimal newAmount = mtUser.getBalance().add(mtBalance.getAmount());
        if (newAmount.compareTo(new BigDecimal("0")) < 0) {
            return false;
        }
        if (mtUser.getStoreId() != null) {
            mtBalance.setStoreId(mtUser.getStoreId());
        }
        mtBalance.setMerchantId(mtUser.getMerchantId());
        if (updateBalance) {
            mtUserMapper.updateUserBalance(mtUser.getMerchantId(), Arrays.asList(mtUser.getId()), mtBalance.getAmount());
            logger.info("会员余额变动：" + mtUser.getMobile() + "，会员ID：" + mtUser.getId() + "，会员余额：" + newAmount);
        }

        if (PhoneFormatCheckUtils.isChinaPhoneLegal(mtUser.getMobile())) {
            mtBalance.setMobile(mtUser.getMobile());
        }
        mtBalanceMapper.insert(mtBalance);

        // 生成充值订单
        if (StringUtil.isEmpty(mtBalance.getOrderSn()) && mtBalance.getAmount().compareTo(new BigDecimal("0")) > 0) {
            OrderDto orderDto = new OrderDto();
            orderDto.setMerchantId(mtBalance.getMerchantId());
            orderDto.setStoreId(mtBalance.getStoreId());
            orderDto.setUserId(mtBalance.getUserId());
            orderDto.setType(OrderTypeEnum.RECHARGE.getKey());
            orderDto.setPlatform(PlatformTypeEnum.PC.getCode());
            orderDto.setOrderMode(OrderModeEnum.ONESELF.getKey());
            orderDto.setAmount(mtBalance.getAmount());
            orderDto.setPayAmount(mtBalance.getAmount());
            orderDto.setPayType(PayTypeEnum.CASH.getKey());
            orderDto.setStatus(OrderStatusEnum.COMPLETE.getKey());
            orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
            orderDto.setOperator(mtBalance.getOperator());
            orderDto.setUsePoint(0);
            MtOrder mtOrder = orderService.saveOrder(orderDto);
            if (mtOrder != null) {
                orderService.setOrderPayed(mtOrder.getId(), null);
            }
        }
        mtUser = mtUserMapper.selectById(mtBalance.getUserId());
        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mtUser.getMobile());
            Map<String, String> params = new HashMap<>();
            String action = "";
            if (mtBalance.getAmount().compareTo(new BigDecimal("0")) > 0) {
                action = "+";
            }
            params.put("amount", action + String.format("%.2f", mtBalance.getAmount()));
            params.put("balance", String.format("%.2f", mtUser.getBalance()));
            sendSmsService.sendSms(mtUser.getMerchantId(), "balance-change", mobileList, params);
        } catch (Exception e) {
            logger.error("余额变动短信发送失败:{}", e.getMessage());
        }

        // 发送小程序订阅消息
        Date nowTime = new Date();
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("amount", mtBalance.getAmount());
        params.put("time", dateTime);
        params.put("tips", "您的余额发生了变动，请留意~");
        weixinService.sendSubscribeMessage(mtBalance.getMerchantId(), mtBalance.getUserId(), mtUser.getOpenId(), WxMessageEnum.BALANCE_CHANGE.getKey(), "pages/user/index", params, nowTime);

        return true;
    }

    /**
     * 发放余额
     *
     * @param accountInfo 账号信息
     * @param object 发放对象，all全部
     * @param userIds 会员ID
     * @param amount 发放金额
     * @param remark 备注
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "发放余额")
    public void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException {
        if (!CommonUtil.isNumeric(amount)) {
            throw new BusinessCheckException("充值金额必须是数字");
        }
        if (!object.equals("all") && StringUtil.isEmpty(userIds)) {
            throw new BusinessCheckException("请先选择会员");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            throw new BusinessCheckException("平台账号不能执行该操作");
        }
        BigDecimal balanceAmount = new BigDecimal(amount);
        if (balanceAmount.compareTo(new BigDecimal(20000)) > 0) {
            throw new BusinessCheckException("单次充值金额不能大于20000");
        }

        List<Integer> userIdArr = new ArrayList<>();
        List<String> userIdList = Arrays.asList(userIds.split(","));
        if (userIdList != null && userIdList.size() > 0) {
            for (String userId : userIdList) {
                if (StringUtil.isNotEmpty(userId) && !userIdArr.contains(Integer.parseInt(userId))) {
                    userIdArr.add(Integer.parseInt(userId));
                }
            }
        }
        // 最多不能超过5000人
        if (userIdArr.size() > 5000) {
            throw new BusinessCheckException("最多不能超过5000人");
        }
        mtUserMapper.updateUserBalance(accountInfo.getMerchantId(), userIdArr, balanceAmount);

        if (userIdArr.size() > 0) {
            for (Integer userId : userIdArr) {
                 MtBalance mtBalance = new MtBalance();
                 mtBalance.setAmount(new BigDecimal(amount));
                 mtBalance.setUserId(userId);
                 mtBalance.setStoreId(accountInfo.getStoreId());
                 mtBalance.setMerchantId(accountInfo.getMerchantId());
                 mtBalance.setDescription(remark);
                 mtBalance.setOperator(accountInfo.getAccountName());
                 addBalance(mtBalance, false);
            }
        } else {
            MtBalance mtBalance = new MtBalance();
            mtBalance.setAmount(new BigDecimal(amount));
            mtBalance.setUserId(0); // userId为0表示全体会员
            mtBalance.setMerchantId(accountInfo.getMerchantId());
            mtBalance.setDescription(remark);
            mtBalance.setOperator(accountInfo.getAccountName());
            mtBalance.setStatus(StatusEnum.ENABLED.getKey());
            mtBalance.setCreateTime(new Date());
            mtBalance.setUpdateTime(new Date());
            mtBalanceMapper.insert(mtBalance);
        }
    }

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    @Override
    public List<MtBalance> getBalanceListByOrderSn(String orderSn) {
        return mtBalanceMapper.getBalanceListByOrderSn(orderSn);
    }
}
