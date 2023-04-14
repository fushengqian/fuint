package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.WxMessageEnum;
import com.fuint.common.service.BalanceService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.WeixinService;
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
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 余额管理业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class BalanceServiceImpl extends ServiceImpl<MtBalanceMapper, MtBalance> implements BalanceService {

    @Resource
    private MtBalanceMapper mtBalanceMapper;

    @Resource
    private MtUserMapper mtUserMapper;

    @Resource
    private WeixinService weixinService;

    @Resource
    private MemberService memberService;

    /**
     * 分页查询余额列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<BalanceDto> queryBalanceListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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

        lambdaQueryWrapper.orderByDesc(MtBalance::getId);
        List<MtBalance> balanceList = mtBalanceMapper.selectList(lambdaQueryWrapper);

        List<BalanceDto> dataList = new ArrayList<>();
        for (MtBalance mtBalance : balanceList) {
            MtUser userInfo = memberService.queryMemberById(mtBalance.getUserId());
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
     * @param mtBalance
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "会员余额变动")
    public Boolean addBalance(MtBalance mtBalance) throws BusinessCheckException {
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

        mtUser.setBalance(newAmount);
        mtUserMapper.updateById(mtUser);

        if (PhoneFormatCheckUtils.isChinaPhoneLegal(mtUser.getMobile())) {
            mtBalance.setMobile(mtUser.getMobile());
        }
        mtBalanceMapper.insert(mtBalance);

        // 发送小程序订阅消息
        Date nowTime = new Date();
        Date sendTime = new Date(nowTime.getTime() + 60000);
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("amount", mtBalance.getAmount());
        params.put("time", dateTime);
        params.put("tips", "您的余额发生了变动，请留意~");
        weixinService.sendSubscribeMessage(mtBalance.getUserId(), mtUser.getOpenId(), WxMessageEnum.BALANCE_CHANGE.getKey(), "pages/user/index", params, sendTime);

        return true;
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
