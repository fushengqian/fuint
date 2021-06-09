package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtUserCoupon;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

   /**
    * mt_user_coupon Repository
    * Created by zach
    * Tue Sep 03 11:34:25 GMT+08:00 2019
    */ 
@Repository 
public interface MtUserCouponRepository extends BaseRepository<MtUserCoupon, Integer> {

      /**
       * 根据分组ID获取发放套数
       *
       * @param groupId
       * @return
       */
      @Query("SELECT t.couponId,count(t.id) as num FROM MtUserCoupon t WHERE t.groupId =:groupId GROUP BY t.couponId")
      List<Object[]> getSendedNum(@Param("groupId") Integer groupId);

       /**
        * 根据卡券ID获取发放套数
        *
        * @param couponId
        * @return
        */
       @Query("SELECT t.couponId,count(t.id) as num FROM MtUserCoupon t WHERE t.couponId =:couponId GROUP BY t.couponId")
       List<Object[]> getPeopleNumByCouponId(@Param("couponId") Integer couponId);

      /**
       * 获取用户的券列表
       *
       * @param userId
       * @return
       * */
      @Query("SELECT t FROM MtUserCoupon t WHERE t.userId =:userId AND t.status IN (:status) ORDER BY id DESC")
      List<MtUserCoupon> getUserCouponList(@Param("userId") Integer userId, @Param("status") List<String> status);

      /**
       * 获取用户的券列表
       *
       * @param userId
       * @return
       * */
      @Query("SELECT t FROM MtUserCoupon t WHERE t.userId =:userId AND t.couponId =:couponId AND t.status IN (:status) ORDER BY id DESC")
      List<MtUserCoupon> getUserCouponListByCouponId(@Param("userId") Integer userId, @Param("couponId") Integer couponId ,@Param("status") List<String> status);

      /**
       * 通过code获取
       *
       * @param code
       * @return
       * */
       @Query("SELECT t FROM MtUserCoupon t WHERE t.code =:code ORDER BY id DESC")
       MtUserCoupon findByCode(@Param("code") String code);

       /**
        * 作废卡券
        *
        * @return
        */
       @Transactional
       @Modifying
       @Query(value = "update MtUserCoupon p set p.status ='D',p.operator =:operator where p.uuid =:uuid and p.couponId in (:couponIds) and p.status='A'")
       int removeUserCoupon(@Param("uuid") String uuid, @Param("couponIds") List<Integer> couponIds, @Param("operator") String operator);

       /**
        * 根据分组ID获取过期数量
        *
        * @param groupId
        * @return
        */
       @Query("SELECT u FROM MtUserCoupon u WHERE u.groupId = :groupId AND (u.status = 'A' OR u.status = 'C')")
       List<MtUserCoupon> queryExpireNumByGroupId(@Param("groupId") Integer groupId);

       /**
        * 根据分组ID获取券ID
        *
        * @param uuid
        * @return
        */
       @Query("SELECT t.couponId FROM MtUserCoupon t WHERE t.uuid =:uuid GROUP BY t.couponId")
       List<Integer> getCouponIdsByUuid(@Param("uuid") String uuid);
}
