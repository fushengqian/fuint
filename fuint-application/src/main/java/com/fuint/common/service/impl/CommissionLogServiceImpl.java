package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.common.dto.CommissionLogDto;
import com.fuint.common.service.CommissionLogService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCommissionLogMapper;
import com.fuint.common.enums.StatusEnum;

import com.fuint.repository.model.MtCommissionLog;
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
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 分销提成记录服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class CommissionLogServiceImpl extends ServiceImpl<MtCommissionLogMapper, MtCommissionLog> implements CommissionLogService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionLogServiceImpl.class);

    private MtCommissionLogMapper mtCommissionLogMapper;

    /**
     * 分页查询记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCommissionLog> queryCommissionLogByPagination(PaginationRequest paginationRequest) {
        Page<MtCommissionLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtCommissionLog::getId);
        List<MtCommissionLog> dataList = mtCommissionLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtCommissionLog> paginationResponse = new PaginationResponse(pageImpl, MtCommissionLog.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加分销提成记录
     *
     * @param commissionLog
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "新增分销提成记录")
    public MtCommissionLog addCommissionLog(MtCommissionLog commissionLog) {
        MtCommissionLog mtCommissionLog = new MtCommissionLog();
        Integer id = mtCommissionLogMapper.insert(mtCommissionLog);
        if (id > 0) {
            return mtCommissionLog;
        } else {
            logger.error("新增分销提成记录失败...");
            return null;
        }
    }

    /**
     * 根据ID获取记录信息
     *
     * @param id
     */
    @Override
    public CommissionLogDto queryCommissionLogById(Integer id) {
        MtCommissionLog mtCommissionLog = mtCommissionLogMapper.selectById(id);
        CommissionLogDto commissionLogDto = null;
        if (mtCommissionLog != null) {
            BeanUtils.copyProperties(mtCommissionLog, commissionLogDto);
        }
        return commissionLogDto;
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @param operator 操作人
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "删除分销提成记录")
    public void deleteCommissionLog(Integer id, String operator) {
        MtCommissionLog mtCommissionLog =  mtCommissionLogMapper.selectById(id);
        if (mtCommissionLog == null) {
            logger.error("删除分销提成记录失败...");
            return;
        }
        mtCommissionLog.setStatus(StatusEnum.DISABLE.getKey());
        mtCommissionLog.setUpdateTime(new Date());
        mtCommissionLogMapper.updateById(mtCommissionLog);
    }
}
