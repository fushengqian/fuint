package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtSmsTemplate;

   /**
    * mt_sms_template Repository
    * Created by zach
    * Sat Apr 18 18:39:27 GMT+08:00 2020
    */ 
@Repository 
public interface MtSmsTemplateRepository extends BaseRepository<MtSmsTemplate, Integer> {
}

