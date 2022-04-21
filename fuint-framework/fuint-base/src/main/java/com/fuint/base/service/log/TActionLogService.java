package com.fuint.base.service.log;

import com.fuint.base.dao.entities.TActionLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;

/**
 * 日志服务接口
 *
 * @author fsq
 * @version $Id: TActionLogService.java
 */
public interface TActionLogService {

    /**
     * 保存实体
     *
     * @param actionLog
     */
    void saveActionLog(TActionLog actionLog);

    /**
     * 获取分页查询数据
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<TActionLog> findLogsByPagination(PaginationRequest paginationRequest);
}
