package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtUserGroup;

   /**
    * mt_user_group Repository
    * Created by zach
    * Mon Mar 15 15:34:23 GMT+08:00 2021
    */ 
@Repository 
public interface MtUserGroupRepository extends BaseRepository<MtUserGroup, Integer> {
}

