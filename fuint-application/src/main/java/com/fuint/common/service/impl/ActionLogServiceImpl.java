package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.param.ActionLogPage;
import com.fuint.common.service.ActionLogService;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.TActionLogMapper;
import com.fuint.repository.model.TActionLog;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 日志服务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class ActionLogServiceImpl extends ServiceImpl<TActionLogMapper, TActionLog> implements ActionLogService {

    private TActionLogMapper tActionLogMapper;

    public void saveActionLog(TActionLog actionLog) {
        tActionLogMapper.insert(actionLog);
    }

    public PaginationResponse<TActionLog> findLogsByPagination(ActionLogPage actionLogPage) {
        Page<TActionLog> pageHelper = PageHelper.startPage(actionLogPage.getPage(), actionLogPage.getPageSize());
        LambdaQueryWrapper<TActionLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        Integer merchantId = actionLogPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(TActionLog::getMerchantId, merchantId);
        }
        Integer storeId = actionLogPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(TActionLog::getStoreId, storeId);
        }
        String keyword = actionLogPage.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            lambdaQueryWrapper.like(TActionLog::getModule, keyword);
        }
        String name = actionLogPage.getAccountName();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.eq(TActionLog::getAcctName, name);
        }
        String startTime = actionLogPage.getBeginTime();
        if (StringUtils.isNotBlank(startTime)) {
            lambdaQueryWrapper.gt(TActionLog::getActionTime, startTime);
        }
        String endTime = actionLogPage.getEndTime();
        if (StringUtils.isNotBlank(endTime)) {
            lambdaQueryWrapper.lt(TActionLog::getActionTime, endTime);
        }
        String ip = actionLogPage.getIp();
        if (StringUtils.isNotBlank(ip)) {
            lambdaQueryWrapper.eq(TActionLog::getClientIp, ip);
        }

        lambdaQueryWrapper.orderByDesc(TActionLog::getId);
        List<TActionLog> dataList = tActionLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(actionLogPage.getPage(), actionLogPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<TActionLog> paginationResponse = new PaginationResponse(pageImpl, TActionLog.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }
}
