package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
    * mt_store Repository
    * Created by zach
    * Thu Jul 18 15:16:30 CST 2019
    */ 
@Repository 
public interface MtStoreRepository extends BaseRepository<MtStore, Integer> {

   /**
    * 根据创建日期查找店铺列表
    *
    * @return
    */
   @Query("select t from MtStore t where t.status = 1 and t.createTime >= :beginTime and t.createTime<= :endTime")
   List<MtStore> queryEffectiveStoreRange(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 根据名称查找店铺列表
    *
    * @return
    */
   @Query("select t from MtStore t where t.name = :storeName")
   MtStore queryStoreByName(@Param("storeName") String storeName);


   /**
    * 根据活动Id获取店铺信息列表
    *
    * @return
    */
   @Query("select t from MtStore t where  t.status ='A' and t.id in (:ids) order by t.id desc")
   List<MtStore> findStoresByIds(@Param("ids") List<Integer> ids);


   /**
    * 根据更新状态
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtStore p set p.status =:statusenum where p.id in (:ids)")
   int updateStatus(@Param("ids") List<Integer> ids, @Param("statusenum") String statusenum);
}

