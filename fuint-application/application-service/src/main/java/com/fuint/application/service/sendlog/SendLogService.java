package com.fuint.application.service.sendlog;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtSendLog;
import com.fuint.application.dto.ReqSendLogDto;

/**
 * 发券记录业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface SendLogService {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加记录
     *
     * @param reqSendLogDto
     * @throws BusinessCheckException
     */
    MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) throws BusinessCheckException;

    /**
     * 根据组ID获取发券记录
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtSendLog querySendLogById(Long id) throws BusinessCheckException;

    /**
     * 删除发券记录
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteSendLog(Long id, String operator) throws BusinessCheckException;
}
