package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TActionLog;
import org.springframework.stereotype.Repository;

/**
 * 日志服务Repository
 * 
 * @author fsq
 * @version $Id: TActionLogRepository.java
 */
@Repository("tActionLogRepository")
public interface TActionLogRepository extends BaseRepository<TActionLog, Long> {

}
