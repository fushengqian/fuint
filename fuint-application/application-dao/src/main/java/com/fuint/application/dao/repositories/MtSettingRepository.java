package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSetting;

import java.util.List;

/**
    * mt_setting Repository
    * Created by zach
    * Tue May 18 22:32:02 GMT+08:00 2021
    */ 
@Repository 
public interface MtSettingRepository extends BaseRepository<MtSetting, Integer> {
   @Query("select t from MtSetting t where t.type = :type")
   List<MtSetting> querySettingByType(@Param("type") String type);

   @Query("select t from MtSetting t where t.name = :name")
   MtSetting querySettingByName(@Param("name") String name);
}

