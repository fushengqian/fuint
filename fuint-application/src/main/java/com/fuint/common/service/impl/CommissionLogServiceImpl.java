package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.common.service.CommissionLogService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCommissionLogMapper;
import com.fuint.common.enums.StatusEnum;

import com.fuint.repository.model.MtCommissionLog;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 分销提成记录服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CommissionLogServiceImpl extends ServiceImpl<MtCommissionLogMapper, MtCommissionLog> implements CommissionLogService {

    private static final Logger logger = LoggerFactory.getLogger(CommissionLogServiceImpl.class);

    @Resource
    private MtCommissionLogMapper mtCommissionLogMapper;

    /**
     * 分页查询记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCommissionLog> queryDataByPagination(PaginationRequest paginationRequest) {
        Page<MtCommissionLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCommissionLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStatus, status);
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
    public MtCommissionLog queryCommissionLogById(Integer id) {
        return mtCommissionLogMapper.selectById(id);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @param operator 操作人
     */
    @Override
    @OperationServiceLog(description = "删除分销提成记录")
    public void deleteCommissionLog(Integer id, String operator) {
        MtCommissionLog mtCommissionLog = queryCommissionLogById(id);
        if (mtCommissionLog == null) {
            logger.error("删除分销提成记录失败...");
            return;
        }
        mtCommissionLog.setStatus(StatusEnum.DISABLE.getKey());
        mtCommissionLog.setUpdateTime(new Date());
        mtCommissionLogMapper.updateById(mtCommissionLog);
    }

    /**
     * 更新分销提成记录
     *
     * @param  commissionLog
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新分销提成记录")
    public MtCommissionLog updateCommissionLog(MtCommissionLog commissionLog) throws BusinessCheckException {
        MtCommissionLog mtCommissionLog = queryCommissionLogById(commissionLog.getId());
        if (mtCommissionLog == null) {
            logger.error("更新分销提成记录失败...");
            throw new BusinessCheckException("该数据状态异常");
        }
        mtCommissionLog.setId(commissionLog.getId());
        if (commissionLog.getStoreId() != null) {
            mtCommissionLog.setStoreId(commissionLog.getStoreId());
        }
        if (commissionLog.getDescription() != null) {
            mtCommissionLog.setDescription(commissionLog.getDescription());
        }
        if (commissionLog.getOperator() != null) {
            mtCommissionLog.setOperator(commissionLog.getOperator());
        }
        if (commissionLog.getStatus() != null) {
            mtCommissionLog.setStatus(commissionLog.getStatus());
        }
        mtCommissionLog.setUpdateTime(new Date());
        mtCommissionLogMapper.updateById(mtCommissionLog);
        return mtCommissionLog;
    }

    @Override
    public List<MtCommissionLog> queryDataByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();

        LambdaQueryWrapper<MtCommissionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCommissionLog::getStatus, status);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                              .eq(MtCommissionLog::getStoreId, 0)
                              .or()
                              .eq(MtCommissionLog::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtCommissionLog::getId);
        List<MtCommissionLog> dataList = mtCommissionLogMapper.selectList(lambdaQueryWrapper);

        return dataList;
    }
}
