package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtConfirmer;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
    * mt_confirmer_personel Repository
    * Created by zach
    * Tue Sep 10 16:40:57 CST 2019
    */ 
@Repository 
public interface MtConfirmerRepository extends BaseRepository<MtConfirmer, Integer> {

      /**
       * 根据更新状态
       *
       * @return
       */
      @Transactional
      @Modifying
      @Query(value = "update MtConfirmer p set p.auditedStatus =:statusenum,p.updateTime=:currentDT where p.id in (:ids)")
      int updateStatus(@Param("ids") List<Integer> ids, @Param("statusenum") String statusenum,@Param("currentDT") Date currentDT );

      /**
       * 根据名称查找核销人员
       *
       * @return
       */
      @Query("select t from MtConfirmer t where t.mobile = :mobile")
      MtConfirmer queryConfirmerByMobile(@Param("mobile") String mobile);

}

