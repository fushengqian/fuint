package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtCoupon;

import java.util.List;

/**
 * mt_coupon Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
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
    * 根据店铺id获取列表
    *
    * @param storeId
    * @return
    */
   @Query("SELECT t FROM MtCoupon t WHERE find_in_set(:storeId, t.storeIds)>0 AND t.status='A'")
   List<MtCoupon> queryByStoreId(@Param("storeId") String storeId);
}

