package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtConfirmLog;

   /**
    * mt_confirm_log Repository
    * Created by shude.wang
    * Fri Oct 18 10:03:14 CST 2019
    */ 
@Repository 
public interface MtConfirmLogRepository extends BaseRepository<MtConfirmLog, Integer> {
}

