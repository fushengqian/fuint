package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.WxMessageEnum;
import com.fuint.common.service.BalanceService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SendSmsService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.PhoneFormatCheckUtils;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBalanceMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtBalance;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@AllArgsConstructor
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
     * 分页查询余额列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<BalanceDto> queryBalanceListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        LambdaQueryWrapper<MtBalance> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBalance::getStatus, StatusEnum.DISABLE.getKey());

        String description = paginationRequest.getSearchParams().get("description") == null ? "" : paginationRequest.getSearchParams().get("description").toString();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtBalance::getDescription, description);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBalance::getStatus, status);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtBalance::getUserId, userId);
        }
        String orderSn = paginationRequest.getSearchParams().get("orderSn") == null ? "" : paginationRequest.getSearchParams().get("orderSn").toString();
        if (StringUtils.isNotBlank(orderSn)) {
            lambdaQueryWrapper.eq(MtBalance::getOrderSn, orderSn);
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtBalance::getMobile, mobile);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtBalance::getMerchantId, merchantId);
        }
        String userNo = paginationRequest.getSearchParams().get("userNo") == null ? "" : paginationRequest.getSearchParams().get("userNo").toString();
        if (StringUtil.isNotEmpty(userNo)) {
            if (StringUtil.isEmpty(merchantId)) {
                merchantId = "0";
            }
            MtUser userInfo = memberService.queryMemberByUserNo(Integer.parseInt(merchantId), userNo);
            if (userInfo != null) {
                lambdaQueryWrapper.eq(MtBalance::getUserId, userInfo.getId());
            } else {
                lambdaQueryWrapper.eq(MtBalance::getUserId, -1);
            }
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtBalance::getStoreId, storeId);
        }
        lambdaQueryWrapper.orderByDesc(MtBalance::getId);
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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
        mtBalance.setStatus(StatusEnum.ENABLED.getKey());
        mtBalance.setCreateTime(new Date());
        mtBalance.setUpdateTime(new Date());

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
            mtUser.setBalance(newAmount);
            mtUserMapper.updateById(mtUser);
        }

        if (PhoneFormatCheckUtils.isChinaPhoneLegal(mtUser.getMobile())) {
            mtBalance.setMobile(mtUser.getMobile());
        }
        mtBalanceMapper.insert(mtBalance);

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
        Date sendTime = new Date(nowTime.getTime() + 60000);
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("amount", mtBalance.getAmount());
        params.put("time", dateTime);
        params.put("tips", "您的余额发生了变动，请留意~");
        weixinService.sendSubscribeMessage(mtBalance.getMerchantId(), mtBalance.getUserId(), mtUser.getOpenId(), WxMessageEnum.BALANCE_CHANGE.getKey(), "pages/user/index", params, sendTime);

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
