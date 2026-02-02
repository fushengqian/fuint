package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ConfirmLogDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ConfirmLogPage;
import com.fuint.common.service.ConfirmLogService;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtConfirmLogMapper;
import com.fuint.repository.model.MtConfirmLog;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 核销卡券服务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class ConfirmLogServiceImpl extends ServiceImpl<MtConfirmLogMapper, MtConfirmLog> implements ConfirmLogService {

    private MtConfirmLogMapper mtConfirmLogMapper;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 分页查询卡券核销列表
     *
     * @param confirmLogPage
     * @return
     */
    @Override
    public PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(ConfirmLogPage confirmLogPage) {
        Page<MtConfirmLog> pageHelper = PageHelper.startPage(confirmLogPage.getPage(), confirmLogPage.getPageSize());
        LambdaQueryWrapper<MtConfirmLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtConfirmLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = confirmLogPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtConfirmLog::getStatus, status);
        }
        Integer userId = confirmLogPage.getUserId();
        if (userId != null) {
            lambdaQueryWrapper.eq(MtConfirmLog::getUserId, userId);
        }
        Integer couponId = confirmLogPage.getCouponId();
        if (couponId != null && couponId > 0) {
            lambdaQueryWrapper.eq(MtConfirmLog::getCouponId, couponId);
        }
        Integer merchantId = confirmLogPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtConfirmLog::getMerchantId, merchantId);
        }
        Integer storeId = confirmLogPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtConfirmLog::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtConfirmLog::getId);
        List<MtConfirmLog> confirmLogList = mtConfirmLogMapper.selectList(lambdaQueryWrapper);
        List<ConfirmLogDto> dataList = new ArrayList<>();

        for (MtConfirmLog log : confirmLogList) {
             MtUser userInfo = memberService.queryMemberById(log.getUserId());
             MtStore storeInfo = storeService.queryStoreById(log.getStoreId());
             MtCoupon couponInfo = couponService.queryCouponById(log.getCouponId());
             ConfirmLogDto item = new ConfirmLogDto();
             item.setId(log.getId());
             item.setCode(log.getCode());
             item.setUserInfo(userInfo);
             item.setStoreInfo(storeInfo);
             item.setCouponInfo(couponInfo);
             item.setUserCouponId(log.getUserCouponId());
             item.setAmount(log.getAmount());
             item.setCreateTime(log.getCreateTime());
             item.setUpdateTime(log.getUpdateTime());
             item.setStatus(log.getStatus());
             item.setRemark(log.getRemark());
             item.setOperator(log.getOperator());
             dataList.add(item);
        }

        PageRequest pageRequest = PageRequest.of(confirmLogPage.getPage(), confirmLogPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<ConfirmLogDto> paginationResponse = new PaginationResponse(pageImpl, ConfirmLogDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 获取卡券（计次卡）核销次数
     * @param userCouponId 会员卡券ID
     * @return
     * */
    @Override
    public Long getConfirmNum(Integer userCouponId) {
        if (userCouponId > 0) {
            return mtConfirmLogMapper.getConfirmNum(userCouponId);
        } else {
            return 0L;
        }
    }

    /**
     * 获取卡券核销列表
     * @param userCouponId
     * @return
     * */
    @Override
    public List<MtConfirmLog> getConfirmList(Integer userCouponId) {
        if (userCouponId == null || userCouponId <= 0) {
            return new ArrayList<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        params.put("USER_COUPON_ID", userCouponId.toString());
        return mtConfirmLogMapper.selectByMap(params);
    }

    /**
     * 获取卡券核销数量
     * @param merchantId 商户ID
     * @param storeId    店铺ID
     * @param beginTime  开始时间
     * @param endTime    结束时间
     * @return
     * */
    @Override
    public Long getConfirmCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) {
        return mtConfirmLogMapper.getConfirmLogCount(merchantId, storeId, beginTime, endTime);
    }
}
