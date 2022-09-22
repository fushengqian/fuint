package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtUser;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * mt_user Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtUserRepository extends BaseRepository<MtUser, Integer> {

   /**
    * 根据手机号码查找会员
    *
    * @return
    */
   @Query("select t from MtUser t where t.mobile = :mobile")
   List<MtUser> queryMemberByMobile(@Param("mobile") String mobile);

   /**
    * 根据名称查找会员
    *
    * @return
    */
   @Query("select t from MtUser t where t.name = :name and t.source = 'register_by_account'")
   List<MtUser> queryMemberByName(@Param("name") String name);

   /**
    * 根据openId查找会员
    *
    * @return
    */
   @Query("select t from MtUser t where t.openId = :openId")
   MtUser queryMemberByOpenId(@Param("openId") String openId);

   /**
    * 根据会员Ids 列表获取会员列表
    *
    * @return
    */
   @Query("select t from MtUser t where t.userNo =:userNo")
   List<MtUser> findMembersByUserNo(@Param("userNo") String userNo);

   /**
    * 根据单独ID获取会员信息
    *
    * @return
    */
   @Query("select t from MtUser t where t.id = :id order by t.id desc")
   MtUser findMembersById(@Param("id") Integer id);

   /**
    * 更新登录时间
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtUser p set p.updateTime =:updateTime where p.id = :id")
   int updateActiveTime(@Param("id") Integer id, @Param("updateTime") Date updateTime);

   /**
    * 获取会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t WHERE t.status = 'A'")
   Long getUserCount();

   /**
    * 获取会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t WHERE t.storeId = :storeId and t.status = 'A'")
   Long getUserCount(@Param("storeId") Integer storeId);

   /**
    * 获取会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t where t.status='A' and t.createTime <= :endTime and t.createTime >= :beginTime")
   Long getUserCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t where t.storeId = :storeId and t.status='A' and t.createTime <= :endTime and t.createTime >= :beginTime")
   Long getUserCount(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取活跃会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t where t.status='A' and t.updateTime <= :endTime and t.updateTime >= :beginTime")
   Long getActiveUserCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取活跃会员总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtUser t where t.storeId = :storeId and t.status='A' and t.updateTime <= :endTime and t.updateTime >= :beginTime")
   Long getActiveUserCount(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}

