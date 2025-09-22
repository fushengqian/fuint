package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.service.CommissionRelationService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCommissionRelationMapper;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.model.MtCommissionRelation;
import com.fuint.repository.model.MtUser;
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
import java.util.*;

/**
 * 分销提成关系服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class CommissionRelationServiceImpl extends ServiceImpl<MtCommissionRelationMapper, MtCommissionRelation> implements CommissionRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionRelationServiceImpl.class);

    private MtCommissionRelationMapper mtCommissionRelationMapper;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 分页查询关系列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<CommissionRelationDto> queryRelationByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtCommissionRelation> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionRelation> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionRelation::getStatus, StatusEnum.DISABLE.getKey());
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionRelation::getStatus, status);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtCommissionRelation::getUserId, userId);
        }
        String subUserId = paginationRequest.getSearchParams().get("subUserId") == null ? "" : paginationRequest.getSearchParams().get("subUserId").toString();
        if (StringUtils.isNotBlank(subUserId)) {
            lambdaQueryWrapper.eq(MtCommissionRelation::getSubUserId, subUserId);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();

        String merchantNo = paginationRequest.getSearchParams().get("merchantNo") == null ? "" : paginationRequest.getSearchParams().get("merchantNo").toString();
        if (StringUtils.isNotBlank(merchantNo) && StringUtil.isEmpty(merchantId)) {
            Integer mchId = merchantService.getMerchantId(merchantNo);
            if (mchId != null && mchId > 0) {
                merchantId = mchId.toString();
            }
        }

        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtCommissionRelation::getMerchantId, merchantId);
        }

        lambdaQueryWrapper.orderByDesc(MtCommissionRelation::getId);
        List<MtCommissionRelation> relationList = mtCommissionRelationMapper.selectList(lambdaQueryWrapper);
        List<CommissionRelationDto> dataList = new ArrayList<>();
        if (relationList != null && relationList.size() > 0) {
            for (MtCommissionRelation mtCommissionRelation : relationList) {
                 CommissionRelationDto commissionRelationDto = new CommissionRelationDto();
                 BeanUtils.copyProperties(mtCommissionRelation, commissionRelationDto);
                 MtUser userInfo = memberService.queryMemberById(mtCommissionRelation.getUserId());
                 MtUser subUserInfo = memberService.queryMemberById(mtCommissionRelation.getSubUserId());
                 if (userInfo != null && subUserInfo != null) {
                     commissionRelationDto.setUserInfo(userInfo);
                     commissionRelationDto.setSubUserInfo(subUserInfo);
                     dataList.add(commissionRelationDto);
                 }
            }
        }
        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<CommissionRelationDto> paginationResponse = new PaginationResponse(pageImpl, CommissionRelationDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 设置分销提成关系
     *
     * @param userInfo 会员信息
     * @param shareId 分享者ID
     * @throws BusinessCheckException
     * @retrurn
     */
    @Override
    public void setCommissionRelation(MtUser userInfo, String shareId) throws BusinessCheckException {
        if (userInfo == null || StringUtil.isBlank(shareId) || Integer.parseInt(shareId) <= 0) {
            return;
        }

        MtUser shareUserInfo = memberService.queryMemberById(Integer.parseInt(shareId));
        if (shareUserInfo == null) {
            return;
        }

        Map<String, Object> param = new HashMap();
        param.put("USER_ID", Integer.parseInt(shareId));
        param.put("SUB_USER_ID", userInfo.getId());
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtCommissionRelation> dataList = mtCommissionRelationMapper.selectByMap(param);
        if (dataList == null || dataList.size() <= 0) {
            MtCommissionRelation mtCommissionRelation = new MtCommissionRelation();
            mtCommissionRelation.setCreateTime(new Date());
            mtCommissionRelation.setUpdateTime(new Date());
            mtCommissionRelation.setStatus(StatusEnum.ENABLED.getKey());
            mtCommissionRelation.setUserId(Integer.parseInt(shareId));
            mtCommissionRelation.setSubUserId(userInfo.getId());
            mtCommissionRelation.setMerchantId(userInfo.getMerchantId());
            mtCommissionRelation.setInviteCode(shareUserInfo.getUserNo());
            mtCommissionRelation.setLevel(1);
            mtCommissionRelationMapper.insert(mtCommissionRelation);
        }

        Map<String, Object> params = new HashMap();
        params.put("SUB_USER_ID", Integer.parseInt(shareId));
        params.put("LEVEL", 1);
        params.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtCommissionRelation> data = mtCommissionRelationMapper.selectByMap(params);
        if (data != null && data.size() > 0) {
            MtCommissionRelation mtCommissionRelation = new MtCommissionRelation();
            mtCommissionRelation.setCreateTime(new Date());
            mtCommissionRelation.setUpdateTime(new Date());
            mtCommissionRelation.setStatus(StatusEnum.ENABLED.getKey());
            mtCommissionRelation.setUserId(data.get(0).getUserId());
            mtCommissionRelation.setSubUserId(userInfo.getId());
            mtCommissionRelation.setMerchantId(userInfo.getMerchantId());
            mtCommissionRelation.setInviteCode(data.get(0).getInviteCode());
            mtCommissionRelation.setLevel(2);
            mtCommissionRelationMapper.insert(mtCommissionRelation);
        }

        return;
    }
}
