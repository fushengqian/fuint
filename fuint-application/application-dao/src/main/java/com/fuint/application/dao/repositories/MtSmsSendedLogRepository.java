package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSmsSendedLog;

   /**
    * mt_sms_sended_log Repository
    * Created by zach
    * Thu Sep 19 15:37:11 CST 2019
    */ 
@Repository 
public interface MtSmsSendedLogRepository extends BaseRepository<MtSmsSendedLog, Integer> {
}

