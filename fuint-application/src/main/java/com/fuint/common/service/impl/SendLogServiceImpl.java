package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.coupon.ReqSendLogDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.SendLogPage;
import com.fuint.common.service.SendLogService;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtSendLogMapper;
import com.fuint.repository.model.MtSendLog;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 发送卡券记录业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class SendLogServiceImpl extends ServiceImpl<MtSendLogMapper, MtSendLog> implements SendLogService {

    private MtSendLogMapper mtSendLogMapper;

    /**
     * 分页查询列表
     *
     * @param sendLogPage
     * @return
     */
    @Override
    public PaginationResponse<MtSendLog> querySendLogListByPagination(SendLogPage sendLogPage) {
        Page<MtSendLog> pageHelper = PageHelper.startPage(sendLogPage.getPage(), sendLogPage.getPageSize());
        LambdaQueryWrapper<MtSendLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtSendLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = sendLogPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtSendLog::getStatus, status);
        }
        Integer userId = sendLogPage.getUserId();
        if (userId != null) {
            lambdaQueryWrapper.eq(MtSendLog::getUserId, userId);
        }
        Integer merchantId = sendLogPage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtSendLog::getMerchantId, merchantId);
        }
        Integer storeId = sendLogPage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtSendLog::getStoreId, storeId);
        }
        Integer couponId = sendLogPage.getCouponId();
        if (couponId != null) {
            lambdaQueryWrapper.eq(MtSendLog::getCouponId, couponId);
        }
        String mobile = sendLogPage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtSendLog::getMobile, mobile);
        }

        lambdaQueryWrapper.orderByDesc(MtSendLog::getId);
        List<MtSendLog> dataList = mtSendLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(sendLogPage.getPage(), sendLogPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtSendLog> paginationResponse = new PaginationResponse(pageImpl, MtSendLog.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加发放记录
     *
     * @param  reqSendLogDto
     * @return
     */
    @Override
    public MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) {
        MtSendLog mtLog = new MtSendLog();
        mtLog.setMerchantId(reqSendLogDto.getMerchantId());
        mtLog.setStoreId(reqSendLogDto.getStoreId());
        mtLog.setType(reqSendLogDto.getType());
        mtLog.setUserId(reqSendLogDto.getUserId());
        mtLog.setFileName(reqSendLogDto.getFileName());
        mtLog.setFilePath(reqSendLogDto.getFilePath());
        mtLog.setMobile(reqSendLogDto.getMobile());
        mtLog.setCouponId(reqSendLogDto.getCouponId());
        mtLog.setGroupId(reqSendLogDto.getGroupId());
        mtLog.setGroupName(reqSendLogDto.getGroupName());
        mtLog.setSendNum(reqSendLogDto.getSendNum());
        mtLog.setRemoveSuccessNum(0);
        mtLog.setRemoveFailNum(0);
        mtLog.setStatus(StatusEnum.ENABLED.getKey());
        mtLog.setCreateTime(new Date());
        mtLog.setOperator(reqSendLogDto.getOperator());
        mtLog.setUuid(reqSendLogDto.getUuid());
        mtSendLogMapper.insert(mtLog);
        return mtLog;
    }

    /**
     * 根据ID查询发券记录
     *
     * @param id 发券记录ID
     * @return
     */
    @Override
    public MtSendLog querySendLogById(Long id) {
        return mtSendLogMapper.selectById(id.intValue());
    }
}
