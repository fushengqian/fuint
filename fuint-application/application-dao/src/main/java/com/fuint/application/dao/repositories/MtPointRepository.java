package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtPoint;

   /**
    * mt_point Repository
    * Created by zach
    * Tue May 18 23:09:52 GMT+08:00 2021
    */ 
@Repository 
public interface MtPointRepository extends BaseRepository<MtPoint, Integer> {
}

