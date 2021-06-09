package com.fuint.base.dao.repositories;


import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TActionLog;
import org.springframework.stereotype.Repository;

/**
 * 日志服务Repository
 * 
 * @author fsq
 * @version $Id: TActionLogRepository.java, v 0.1 2015年12月3日 上午10:27:11 fsq Exp $
 */
@Repository("tActionLogRepository")
public interface TActionLogRepository extends BaseRepository<TActionLog, Long> {

}
