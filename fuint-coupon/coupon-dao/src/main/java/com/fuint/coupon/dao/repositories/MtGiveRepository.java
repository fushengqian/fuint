package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtGive;

import java.util.Date;
import java.util.List;

/**
    * mt_give Repository
    * Created by zach
    * Sat Oct 12 15:34:19 GMT+08:00 2019
    */ 
@Repository 
public interface MtGiveRepository extends BaseRepository<MtGive, Integer> {

   /**
    * 查询是否重复
    *
    * @param userId
    * @return
    */
   @Query("SELECT t FROM MtGive t WHERE t.userId = :userId AND t.giveUserId = :giveUserId AND t.couponIds = :couponIds AND t.createTime >= :createTime")
   List<MtGive> queryForUnique(@Param("userId") Integer userId, @Param("giveUserId") Integer giveUserId, @Param("couponIds") String couponIds, @Param("createTime") Date createTime);
}

