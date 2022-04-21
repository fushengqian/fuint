package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrder;
import java.math.BigDecimal;
import java.util.Date;

/**
 * mt_order Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtOrderRepository extends BaseRepository<MtOrder, Integer> {
   /**
    * 获取订单总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtOrder t")
   BigDecimal getOrderCount();

   /**
    * 获取订单总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtOrder t WHERE t.storeId = :storeId")
   BigDecimal getOrderCount(@Param("storeId") Integer storeId);

   /**
    * 获取订单数量
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtOrder t WHERE t.createTime <= :endTime and t.createTime >= :beginTime")
   BigDecimal getOrderCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取订单数量
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtOrder t WHERE t.storeId = :storeId and t.createTime <= :endTime and t.createTime >= :beginTime")
   BigDecimal getOrderCount(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 根据订单号查询订单
    *
    * @return
    * */
   @Query("select t from MtOrder t where t.orderSn = :orderSn")
   MtOrder findByOrderSn(@Param("orderSn") String orderSn);

   /**
    * 获取订单支付金额
    *
    * @return
    */
   @Query("SELECT sum(t.amount) as num FROM MtOrder t where t.payStatus='B' and t.payTime <= :endTime and t.payTime >= :beginTime")
   BigDecimal getPayMoney(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取订单支付金额
    *
    * @return
    */
   @Query("SELECT sum(t.amount) as num FROM MtOrder t where t.storeId = :storeId and t.payStatus='B' and t.payTime <= :endTime and t.payTime >= :beginTime")
   BigDecimal getPayMoney(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取订单支付总金额
    *
    * @return
    */
   @Query("SELECT sum(t.amount) as num FROM MtOrder t where t.payStatus='B'")
   BigDecimal getPayMoney();

   /**
    * 获取订单支付总金额
    *
    * @return
    */
   @Query("SELECT sum(t.amount) as num FROM MtOrder t where t.storeId = :storeId and t.payStatus='B'")
   BigDecimal getPayMoney(@Param("storeId") Integer storeId);

   /**
    * 获取订单支付人数
    *
    * @return
    */
   @Query("SELECT COUNT(DISTINCT t.userId) as num FROM MtOrder t where t.payStatus='B'")
   Integer getPayUserCount();

   /**
    * 获取订单支付人数
    *
    * @return
    */
   @Query("SELECT COUNT(DISTINCT t.userId) as num FROM MtOrder t where t.storeId = :storeId and t.payStatus='B'")
   Integer getPayUserCount(@Param("storeId") Integer storeId);
}

