package com.fuint.application.service.point;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtPointRepository;
import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.dto.ConfirmLogDto;
import com.fuint.application.dto.PointDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 积分管理业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    @Autowired
    private MtPointRepository pointRepository;

    @Autowired
    private MtUserRepository userRepository;

    @Autowired
    private MemberService memberService;

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<PointDto> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtPoint> paginationResponse = pointRepository.findResultsByPagination(paginationRequest);

        List<PointDto> content = new ArrayList<>();
        List<MtPoint> dataList = paginationResponse.getContent();
        for (MtPoint point : dataList) {
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
            content.add(item);
        }

        PageRequest pageRequest = new PageRequest((paginationRequest.getCurrentPage() +1), paginationRequest.getPageSize());
        Page page = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<PointDto> result = new PaginationResponse(page, ConfirmLogDto.class);
        result.setTotalPages(paginationResponse.getTotalPages());
        result.setContent(content);

        return result;
    }

    /**
     * 添加积分记录
     *
     * @param mtPoint
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void addPoint(MtPoint mtPoint) {
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

        MtUser user = userRepository.findOne(mtPoint.getUserId());
        Integer newAmount = user.getPoint() + mtPoint.getAmount();
        if (newAmount < 0) {
            return;
        }
        user.setPoint(newAmount);

        userRepository.save(user);
        pointRepository.save(mtPoint);
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
    @Transactional
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

        Integer newAmount = fUserInfo.getPoint() + amount;
        if (newAmount < 0) {
            return false;
        }
        fUserInfo.setPoint(newAmount);

        Integer myNewAmount = userInfo.getPoint() - amount;
        if (myNewAmount < 0) {
            return false;
        }
        userInfo.setPoint(myNewAmount);

        userRepository.save(fUserInfo);
        userRepository.save(userInfo);

        MtPoint fMtPoint = new MtPoint();
        fMtPoint.setStatus(StatusEnum.ENABLED.getKey());
        fMtPoint.setAmount(amount);
        fMtPoint.setCreateTime(new Date());
        fMtPoint.setUpdateTime(new Date());
        fMtPoint.setOperator(userInfo.getName());
        fMtPoint.setOrderSn("");
        fMtPoint.setDescription(remark);
        fMtPoint.setUserId(fUserInfo.getId());
        pointRepository.save(fMtPoint);

        MtPoint mtPoint = new MtPoint();
        mtPoint.setUserId(userId);
        mtPoint.setAmount(-amount);
        mtPoint.setStatus(StatusEnum.ENABLED.getKey());
        mtPoint.setCreateTime(new Date());
        mtPoint.setUpdateTime(new Date());
        mtPoint.setOperator(userInfo.getName());
        mtPoint.setOrderSn("");
        mtPoint.setDescription("转赠好友");
        pointRepository.save(mtPoint);

        return true;
    }
}
