package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.coupon.dao.entities.MtCoupon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.TAccount;

import java.util.List;

/**
    * t_account Repository
    * Created by zach
    * Tue Sep 17 21:50:18 CST 2019
    */ 
@Repository 
public interface TAccountRepository extends BaseRepository<TAccount, Integer> {

   /**
    * 根据店铺id获取列表 20190917 zach add
    *
    * @param storeId
    * @return
    */
   @Query("SELECT t FROM TAccount t WHERE storeId=:storeId AND t.accountStatus='1'")
   List<TAccount> queryTAccountByStoreId(@Param("storeId") Integer storeId);
}

