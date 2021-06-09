package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrder;

   /**
    * mt_order Repository
    * Created by zach
    * Wed May 05 22:00:40 GMT+08:00 2021
    */ 
@Repository 
public interface MtOrderRepository extends BaseRepository<MtOrder, Integer> {
}

