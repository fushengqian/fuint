package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.UvCouponInfo;

import java.util.List;

/**
    * uv_coupon_info Repository
    * Created by zach
    * Thu Sep 12 17:18:55 CST 2019
    */ 
@Repository 
public interface MtCouponInfoRepository extends BaseRepository<UvCouponInfo, Integer> {

   /**
    * 根据活动Id获取用户优惠券信息列表
    *
    * @return
    */
   @Query("select t from UvCouponInfo t where t.id in (:ids) order by t.id desc")
   public List<UvCouponInfo> findUvCouponInfoByIds(@Param("ids") List<Integer> ids);

}

