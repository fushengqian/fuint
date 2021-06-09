package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSendLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
    * mt_send_Log Repository
    * Created by zach
    * Mon Sep 16 17:22:38 GMT+08:00 2019
    */ 
@Repository 
public interface MtSendLogRepository extends BaseRepository<MtSendLog, Integer> {

   /**
    * 作废卡券
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtSendLog p set p.status =:status,p.removeSuccessNum =:removeSuccessNum,p.removeFailNum =:removeFailNum where p.uuid =:uuid")
   int updateForRemove(@Param("uuid") String uuid, @Param("status") String status, @Param("removeSuccessNum") Integer removeSuccessNum, @Param("removeFailNum") Integer removeFailNum);


   /**
    * 作废卡券单个用户的券
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtSendLog p set p.status =:status,p.removeSuccessNum =p.removeSuccessNum+1 where p.uuid =:uuid")
   int updateSingleForRemove(@Param("uuid") String uuid, @Param("status") String status);
}

