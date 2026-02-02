package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ReqSendLogDto;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtSendLog;

/**
 * 发券记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SendLogService extends IService<MtSendLog> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest);

    /**
     * 添加记录
     *
     * @param  reqSendLogDto
     * @return
     */
    MtSendLog addSendLog(ReqSendLogDto reqSendLogDto);

    /**
     * 根据组ID获取发券记录
     *
     * @param  id ID
     * @return
     */
    MtSendLog querySendLogById(Long id);

}
