package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.PointDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.WxMessageEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.PointService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtPointMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtPoint;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 积分管理业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class PointServiceImpl extends ServiceImpl<MtPointMapper, MtPoint> implements PointService {

    @Resource
    private MtPointMapper mtPointMapper;

    @Resource
    private MtUserMapper mtUserMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private WeixinService weixinService;

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<PointDto> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtPoint> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtPoint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtPoint::getStatus, StatusEnum.DISABLE.getKey());

        String description = paginationRequest.getSearchParams().get("description") == null ? "" : paginationRequest.getSearchParams().get("description").toString();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtPoint::getDescription, description);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtPoint::getStatus, status);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtPoint::getUserId, userId);
        }

        lambdaQueryWrapper.orderByDesc(MtPoint::getId);
        List<MtPoint> pointList = mtPointMapper.selectList(lambdaQueryWrapper);

        List<PointDto> dataList = new ArrayList<>();
        for (MtPoint point : pointList) {
            MtUser userInfo = memberService.queryMemberById(point.getUserId());
            PointDto item = new PointDto();
            item.setId(point.getId());
            item.setAmount(point.getAmount());
            item.setDescription(point.getDescription());
            item.setCreateTime(point.getCreateTime());
            item.setUpdateTime(point.getUpdateTime());
            item.setUserId(point.getUserId());
            item.setUserInfo(userInfo);
            item.setOrderSn(point.getOrderSn());
            item.setOperator(point.getOperator());
            item.setStatus(point.getStatus());
            dataList.add(item);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<PointDto> paginationResponse = new PaginationResponse(pageImpl, PointDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加积分记录
     *
     * @param mtPoint
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改会员积分")
    public void addPoint(MtPoint mtPoint) throws BusinessCheckException {
        if (mtPoint.getUserId() < 0) {
           return;
        }
        mtPoint.setStatus(StatusEnum.ENABLED.getKey());
        mtPoint.setCreateTime(new Date());
        mtPoint.setUpdateTime(new Date());

        if (mtPoint.getOperator() != null) {
            mtPoint.setOperator(mtPoint.getOperator());
        }

        if (mtPoint.getOrderSn() != null) {
            mtPoint.setOrderSn(mtPoint.getOrderSn());
        }

        MtUser user = mtUserMapper.selectById(mtPoint.getUserId());
        Integer newAmount = user.getPoint() + mtPoint.getAmount();
        if (newAmount < 0) {
            return;
        }
        user.setPoint(newAmount);

        mtUserMapper.updateById(user);
        mtPointMapper.insert(mtPoint);

        // 发送小程序订阅消息
        Date nowTime = new Date();
        Date sendTime = new Date(nowTime.getTime() + 60000);
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("amount", mtPoint.getAmount());
        params.put("time", dateTime);
        params.put("remark", "您的积分发生了变动，请留意~");
        weixinService.sendSubscribeMessage(mtPoint.getUserId(), user.getOpenId(), WxMessageEnum.POINT_CHANGE.getKey(), "pages/user/index", params, sendTime);

        return;
    }

    /**
     * 转赠积分
     *
     * @param userId
     * @param mobile
     * @param amount
     * @param remark
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException {
        if (userId < 0 || StringUtil.isEmpty(mobile) || amount <= 0) {
            return false;
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        MtUser fUserInfo = memberService.queryMemberByMobile(mobile);
        // 自动注册会员
        if (fUserInfo == null) {
            fUserInfo = memberService.addMemberByMobile(mobile);
        }

        if (fUserInfo == null) {
            throw new BusinessCheckException("转赠的好友信息不存在");
        }

        if (fUserInfo.getId().equals(userInfo.getId())) {
            throw new BusinessCheckException("积分不能转赠给自己");
        }

        Integer newAmount = fUserInfo.getPoint() + amount;
        if (newAmount < 0) {
            throw new BusinessCheckException("积分赠送失败");
        }
        fUserInfo.setPoint(newAmount);

        Integer myNewAmount = userInfo.getPoint() - amount;
        if (myNewAmount < 0) {
            throw new BusinessCheckException("您的积分不足");
        }
        userInfo.setPoint(myNewAmount);

        mtUserMapper.updateById(fUserInfo);
        mtUserMapper.updateById(userInfo);

        MtPoint fMtPoint = new MtPoint();
        fMtPoint.setStatus(StatusEnum.ENABLED.getKey());
        fMtPoint.setAmount(amount);
        fMtPoint.setCreateTime(new Date());
        fMtPoint.setUpdateTime(new Date());
        fMtPoint.setOperator(userInfo.getName());
        fMtPoint.setOrderSn("");
        fMtPoint.setDescription(remark);
        fMtPoint.setUserId(fUserInfo.getId());
        mtPointMapper.insert(fMtPoint);

        MtPoint mtPoint = new MtPoint();
        mtPoint.setUserId(userId);
        mtPoint.setAmount(-amount);
        mtPoint.setStatus(StatusEnum.ENABLED.getKey());
        mtPoint.setCreateTime(new Date());
        mtPoint.setUpdateTime(new Date());
        mtPoint.setOperator(userInfo.getName());
        mtPoint.setOrderSn("");
        mtPoint.setDescription("转赠好友");
        mtPointMapper.insert(mtPoint);

        return true;
    }
}
