package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.coupon.GiveDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.GivePage;
import com.fuint.common.param.GiveParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGiveItemMapper;
import com.fuint.repository.mapper.MtGiveMapper;
import com.fuint.repository.mapper.MtUserCouponMapper;
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
 * 转赠业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class GiveServiceImpl extends ServiceImpl<MtGiveMapper, MtGive> implements GiveService {

    private static final Logger logger = LoggerFactory.getLogger(GiveServiceImpl.class);

    private MtGiveMapper mtGiveMapper;

    private MtUserCouponMapper mtUserCouponMapper;

    private MtGiveItemMapper mtGiveItemMapper;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 会员等级服务接口
     * */
    private UserGradeService userGradeService;

    /**
     * 短信发送服务接口
     * */
    private SendSmsService sendSmsService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 卡券分组服务接口
     * */
    private CouponGroupService couponGroupService;

    /**
     * 分页查询转赠列表
     *
     * @param givePage
     * @return
     */
    @Override
    public PaginationResponse<GiveDto> queryGiveListByPagination(GivePage givePage) {
        Page<MtGive> pageHelper = PageHelper.startPage(givePage.getPage(), givePage.getPageSize());
        LambdaQueryWrapper<MtGive> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtGive::getStatus, StatusEnum.DISABLE.getKey());

        String status = givePage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtGive::getStatus, status);
        }
        Integer merchantId = givePage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtGive::getMerchantId, merchantId);
        }
        Integer storeId = givePage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtGive::getStoreId, storeId);
        }
        Integer userId = givePage.getUserId();
        if (userId != null) {
            lambdaQueryWrapper.eq(MtGive::getUserId, userId);
        }
        Integer giveUserId = givePage.getGiveUserId();
        if (giveUserId != null) {
            lambdaQueryWrapper.eq(MtGive::getGiveUserId, giveUserId);
        }
        String couponId = givePage.getCouponId();
        if (StringUtils.isNotBlank(couponId)) {
            lambdaQueryWrapper.eq(MtGive::getCouponIds, couponId);
        }
        String mobile = givePage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtGive::getMobile, mobile);
        }
        String userMobile = givePage.getUserMobile();
        if (StringUtils.isNotBlank(userMobile)) {
            lambdaQueryWrapper.eq(MtGive::getUserMobile, userMobile);
        }

        lambdaQueryWrapper.orderByDesc(MtGive::getId);
        List<MtGive> giveList = mtGiveMapper.selectList(lambdaQueryWrapper);
        List<GiveDto> dataList = new ArrayList<>();
        for (MtGive mtGive : giveList) {
             GiveDto giveDto = new GiveDto();
             BeanUtils.copyProperties(mtGive, giveDto);
             giveDto.setCreateTime(DateUtil.formatDate(mtGive.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
             giveDto.setUpdateTime(DateUtil.formatDate(mtGive.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
             giveDto.setMobile(CommonUtil.hidePhone(giveDto.getMobile()));
             giveDto.setUserMobile(CommonUtil.hidePhone(giveDto.getUserMobile()));
             dataList.add(giveDto);
        }

        PageRequest pageRequest = PageRequest.of(givePage.getPage(), givePage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<GiveDto> paginationResponse = new PaginationResponse(pageImpl, GiveDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 卡券转赠
     *
     * @param  giveParam
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject addGive(GiveParam giveParam) throws BusinessCheckException {
        MtGive give = new MtGive();

        String mobile = giveParam.getMobile() == null ? "" : giveParam.getMobile();
        String couponId = giveParam.getCouponId() == null ? "" : giveParam.getCouponId();
        String note = giveParam.getNote() == null ? "" : giveParam.getNote();
        String message = giveParam.getMessage() == null ? "" : giveParam.getMessage();
        Integer userId = giveParam.getUserId() == null ? 0 : giveParam.getUserId();
        Integer storeId = giveParam.getStoreId() == null ? 0 : giveParam.getStoreId();
        Integer merchantId = giveParam.getMerchantId() == null ? 0 : giveParam.getMerchantId();

        if (StringUtil.isEmpty(mobile) || mobile.length() > 11 || mobile.length() < 11) {
            throw new BusinessCheckException("转增对象手机号有误");
        }

        if (couponId == null) {
            throw new BusinessCheckException("转增卡券不能为空");
        }

        String[] couponIds = couponId.split(",");
        if (couponIds.length > 10) {
            throw new BusinessCheckException("转增卡券数量不能超过10张");
        }

        // 如果赠予对象为空，则注册
        MtUser user = memberService.queryMemberByMobile(merchantId, mobile);
        if (null == user) {
            MtUser userInfo = new MtUser();
            userInfo.setMerchantId(merchantId);
            userInfo.setName(mobile);
            userInfo.setMobile(mobile);
            MtUserGrade grade = userGradeService.getInitUserGrade(merchantId);
            if (grade != null) {
                userInfo.setGradeId(grade.getId());
            }
            userInfo.setBalance(new BigDecimal(0));
            userInfo.setStatus(StatusEnum.ENABLED.getKey());
            user = memberService.addMember(userInfo, "0");
        } else {
            if (!user.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                throw new BusinessCheckException("转增对象可能已被禁用");
            }
        }

        if (null == user) {
            throw new BusinessCheckException("创建转增对象用户信息失败");
        }

        if (user.getId() == userId) {
            throw new BusinessCheckException("转增对象不能是自己");
        }

        BigDecimal money = new BigDecimal(0);
        List<String> couponIdList = new ArrayList<>();
        List<String> couponNames = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        List<String> groupNames = new ArrayList<>();

        for (String id : couponIds) {
            MtUserCoupon userCoupon = mtUserCouponMapper.selectById(Integer.parseInt(id));
            MtCoupon coupon = couponService.queryCouponById(userCoupon.getCouponId());
            if (!couponIdList.contains(coupon.getId().toString())) {
                couponIdList.add(coupon.getId().toString());
            }
            if (!couponNames.contains(coupon.getName())) {
                couponNames.add(coupon.getName());
            }
            MtCouponGroup group = couponGroupService.queryCouponGroupById(coupon.getGroupId());
            if (!groupIds.contains(group.getId().toString())) {
                groupIds.add(group.getId().toString());
            }
            if (!groupNames.contains(group.getName())) {
                groupNames.add(group.getName());
            }
            money = money.add(userCoupon.getAmount());
            if (null == userCoupon) {
                throw new BusinessCheckException("转增卡券不存在");
            } else {
                if (!userCoupon.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    throw new BusinessCheckException("转增卡券必须是未使用状态");
                }
                if (!userCoupon.getUserId().toString().equals(userId.toString())) {
                    throw new BusinessCheckException("您的券可能已经转赠出去了");
                }
            }
        }

        MtUser myUser = memberService.queryMemberById(userId);

        give.setMobile(mobile);
        give.setGiveUserId(userId);
        give.setUserId(user.getId());
        give.setMerchantId(merchantId);
        give.setStoreId(storeId);
        give.setMoney(money);
        give.setNum(couponIds.length);
        give.setNote(note);
        give.setMessage(message);
        give.setUserMobile(myUser.getMobile());
        String couponIdsStr = StringUtil.join(couponIdList.toArray(), ",");
        give.setGroupIds(StringUtil.join(groupIds.toArray(), ","));
        give.setGroupNames(StringUtil.join(groupNames.toArray(), ","));
        give.setCouponIds(couponIdsStr);
        give.setCouponNames(StringUtil.join(couponNames.toArray(), ","));
        give.setStatus(StatusEnum.ENABLED.getKey());
        Date createTime = new Date();
        give.setCreateTime(createTime);
        give.setUpdateTime(createTime);

        // 防止网络延迟，检查是否重复
        List<MtGive> uniqueData = mtGiveMapper.queryForUnique(give.getUserId(), give.getGiveUserId(), couponIdsStr, createTime);
        if (uniqueData != null) {
            if (uniqueData.size() > 0) {
                throw new BusinessCheckException("当前网络延迟，不可重复操作");
            }
        }

        this.save(give);
        MtGive giveInfo = mtGiveMapper.selectById(give.getId());

        for (String id : couponIds) {
            MtUserCoupon userCoupon = mtUserCouponMapper.selectById(Integer.parseInt(id));
            userCoupon.setUserId(user.getId());
            userCoupon.setUpdateTime(new Date());
            userCoupon.setMobile(user.getMobile());
            mtUserCouponMapper.updateById(userCoupon);

            MtGiveItem item = new MtGiveItem();
            item.setCreateTime(new Date());
            item.setGiveId(giveInfo.getId());
            item.setStatus(StatusEnum.ENABLED.getKey());
            item.setUpdateTime(new Date());
            item.setUserCouponId(Integer.parseInt(id));

            mtGiveItemMapper.insert(item);
        }

        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mobile);
            Map<String, String> params = new HashMap<>();
            params.put("totalNum", couponIds.length+"");
            params.put("totalMoney", money+"");
            sendSmsService.sendSms(merchantId, "received-coupon", mobileList, params);
        } catch (Exception e) {
            logger.error("核销卡券发送通知消息出错：", e.getMessage());
        }

        return new ResponseObject(200, "", giveInfo);
    }

    /**
     * 根据ID获取转赠信息
     *
     * @param id ID
     * @return
     */
    @Override
    public MtGive queryGiveById(Long id) {
        return mtGiveMapper.selectById(id.intValue());
    }

    /**
     * 根据条件搜索转赠详情
     *
     * @param params 转赠查询条件
     * @return
     * */
    @Override
    public List<MtGiveItem> queryItemByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtGiveItemMapper.selectByMap(params);
    }
}
