package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrderAddress;
import java.util.List;

/**
 * mt_order_address Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtOrderAddressRepository extends BaseRepository<MtOrderAddress, Integer> {
   /**
    * 根据订单ID查询
    *
    * @return
    */
   @Query("select t from MtOrderAddress t where t.orderId = :orderId")
   List<MtOrderAddress> getOrderAddress(@Param("orderId") Integer orderId);
}

