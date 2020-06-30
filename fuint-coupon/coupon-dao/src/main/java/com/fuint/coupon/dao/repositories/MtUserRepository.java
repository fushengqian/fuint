package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
    * mt_user Repository
    * Created by zach
    * Thu Aug 08 11:15:42 CST 2019
    */ 
@Repository 
public interface MtUserRepository extends BaseRepository<MtUser, Integer> {

   /**
    * 根据创建日期查找会员用户列表
    *
    * @return
    */
   @Query("select t from MtUser t where t.createTime >= :beginTime and t.createTime<= :endTime")
   List<MtUser> queryEffectiveMemberRange(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 根据名称查找会员用户列表
    *
    * @return
    */
   @Query("select t from MtUser t where t.mobile = :mobile")
   MtUser queryMemberByMobile(@Param("mobile") String mobile);


   /**
    * 根据会员Ids 列表获取会员列表
    *
    * @return
    */
   @Query("select t from MtUser t where t.id in (:ids) order by t.id desc")
   List<MtUser> findMembersByIds(@Param("ids") List<Integer> ids);


   /**
    * 根据单独ID获取会员列表
    *
    * @return
    */
   @Query("select t from MtUser t where t.id = :id order by t.id desc")
   MtUser findMembersById(@Param("id") Integer id);


   /**
    * 根据更新状态
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtUser p set p.status =:statusenum where p.id in (:ids)")
   int updateStatus(@Param("ids") List<Integer> ids, @Param("statusenum") String statusenum);
}

