package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtConfirmLog;
import java.util.Date;
import java.util.List;

/**
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtConfirmLogRepository extends BaseRepository<MtConfirmLog, Integer> {

   /**
    * 获取核销次数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtConfirmLog t where t.userCouponId =:userCouponId and t.status = 'A'")
   Long getConfirmNum(@Param("userCouponId") Integer userCouponId);

   /**
    * 获取核销总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtConfirmLog t where t.createTime <=:endTime and t.createTime >=:beginTime")
   Long getConfirmLogCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

   /**
    * 获取订单的卡券核销记录
    *
    * @param orderId
    * @return
    * */
   @Query("SELECT t FROM MtConfirmLog t WHERE t.orderId =:orderId ORDER BY t.id DESC")
   List<MtConfirmLog> getOrderConfirmLogList(@Param("orderId") Integer orderId);
}

