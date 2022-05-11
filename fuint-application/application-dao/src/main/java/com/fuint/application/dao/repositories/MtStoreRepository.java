package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtStore;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * mt_store Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtStoreRepository extends BaseRepository<MtStore, Integer> {

   /**
    * 根据名称查找店铺列表
    *
    * @return
    */
   @Query("select t from MtStore t where t.name = :storeName")
   MtStore queryStoreByName(@Param("storeName") String storeName);

   /**
    * 重置默认店铺
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtStore p set p.isDefault = 'N'")
   void resetDefaultStore();

   /**
    * 根据Id获取店铺列表
    *
    * @return
    */
   @Query("select t from MtStore t where  t.status ='A' and t.id in (:ids) order by t.id desc")
   List<MtStore> findStoresByIds(@Param("ids") List<Integer> ids);
}

