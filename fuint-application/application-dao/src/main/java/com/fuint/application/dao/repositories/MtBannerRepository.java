package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtBanner;

   /**
    * mt_banner Repository
    * Created by zach
    * Thu Apr 22 10:35:28 GMT+08:00 2021
    */ 
@Repository 
public interface MtBannerRepository extends BaseRepository<MtBanner, Integer> {
}

