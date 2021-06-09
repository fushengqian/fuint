package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGiveItem;

   /**
    * mt_give_item Repository
    * Created by zach
    * Wed Oct 09 10:08:14 GMT+08:00 2019
    */ 
@Repository 
public interface MtGiveItemRepository extends BaseRepository<MtGiveItem, Integer> {
}

