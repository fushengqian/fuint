package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ReqSendLogDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SendLogService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtSendLogMapper;
import com.fuint.repository.model.MtConfirmLog;
import com.fuint.repository.model.MtSendLog;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 发送卡券记录业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SendLogServiceImpl extends ServiceImpl<MtSendLogMapper, MtSendLog> implements SendLogService {

    @Resource
    private MtSendLogMapper mtSendLogMapper;

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest) {
        Page<MtSendLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtSendLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtSendLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtSendLog::getStatus, status);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtSendLog::getUserId, userId);
        }
        String couponId = paginationRequest.getSearchParams().get("couponId") == null ? "" : paginationRequest.getSearchParams().get("couponId").toString();
        if (StringUtils.isNotBlank(couponId)) {
            lambdaQueryWrapper.eq(MtSendLog::getCouponId, couponId);
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtSendLog::getMobile, mobile);
        }

        lambdaQueryWrapper.orderByDesc(MtSendLog::getId);
        List<MtSendLog> dataList = mtSendLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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
     * @param reqSendLogDto
     * @throws BusinessCheckException
     */
    @Override
    public MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) {
        MtSendLog mtLog = new MtSendLog();

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
     * @param id ID
     * @throws BusinessCheckException
     */
    @Override
    public MtSendLog querySendLogById(Long id) {
        return mtSendLogMapper.selectById(id.intValue());
    }

    /**
     * 根据ID 删除发券记录
     *
     * @param id       发券记录ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    public void deleteSendLog(Long id, String operator) {
        MtSendLog couponGroup = this.querySendLogById(id);

        if (null == couponGroup) {
            return;
        }

        couponGroup.setStatus(StatusEnum.DISABLE.getKey());
        couponGroup.setOperator(operator);
        mtSendLogMapper.updateById(couponGroup);
    }
}
