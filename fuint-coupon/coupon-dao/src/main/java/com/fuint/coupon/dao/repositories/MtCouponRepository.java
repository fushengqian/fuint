package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtCoupon;

import java.util.List;

/**
 * mt_coupon Repository
 * Created by zach
 * Mon Aug 12 14:28:02 GMT+08:00 2019
 */
@Repository
public interface MtCouponRepository extends BaseRepository<MtCoupon, Integer> {

   /**
    * 根据分组ID获取券种类数量
    *
    * @param groupId
    * @return
    */
   @Query("SELECT COUNT(ar.id) FROM MtCoupon ar WHERE ar.groupId =:groupId AND ar.status='A'")
   Long queryNumByGroupId(@Param("groupId") Integer groupId);

   /**
    * 根据分组ID获取券列表
    *
    * @param groupId
    * @return
    */
   @Query("SELECT t FROM MtCoupon t WHERE t.groupId =:groupId")
   List<MtCoupon> queryByGroupId(@Param("groupId") Integer groupId);

   /**
    * 根据店铺id获取列表 20190917 zach add
    *
    * @param storeId
    * @return
    */
   @Query("SELECT t FROM MtCoupon t WHERE find_in_set(:storeId, t.storeIds)>0 AND t.status='A'")
   List<MtCoupon> queryByStoreId(@Param("storeId") String storeId);
}

