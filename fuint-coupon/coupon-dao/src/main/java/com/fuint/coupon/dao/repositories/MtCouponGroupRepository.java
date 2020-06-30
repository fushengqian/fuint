package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtCouponGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

   /**
    * mt_coupon_group Repository
    * Created by zach
    * Wed Aug 28 13:51:33 GMT+08:00 2019
    */ 
@Repository 
public interface MtCouponGroupRepository extends BaseRepository<MtCouponGroup, Integer> {
}

