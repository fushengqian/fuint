package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.TActionLog;

/**
 * 后台日志服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ActionLogService extends IService<TActionLog> {

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
