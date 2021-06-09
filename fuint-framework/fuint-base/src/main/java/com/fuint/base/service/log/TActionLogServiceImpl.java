package com.fuint.base.service.log;

import com.fuint.base.dao.entities.TActionLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.dao.repositories.TActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 日志服务实现类
 *
 * @author fsq
 * @version $Id: TActionLogServiceImpl.java, v 0.1 2015年12月3日 上午10:29:59 fsq Exp $
 */
@Service("sActionLogService")
public class TActionLogServiceImpl implements TActionLogService {

    /**
     * 日志服务Repository
     */
    @Autowired
    private TActionLogRepository tActionLogRepository;

    @Transactional
    public void saveActionLog(TActionLog actionLog) {
        this.tActionLogRepository.save(actionLog);
    }

    public PaginationResponse<TActionLog> findLogsByPagination(PaginationRequest paginationRequest) {
        return tActionLogRepository.findResultsByPagination(paginationRequest);
    }
}
