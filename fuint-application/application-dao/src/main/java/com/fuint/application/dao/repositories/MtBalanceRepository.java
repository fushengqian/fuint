package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtBalance;
import java.util.List;

/**
* mt_balance Repository
* Created by FSQ
* Contact wx fsq_better
* Site https://www.fuint.cn
*/ 
@Repository 
public interface MtBalanceRepository extends BaseRepository<MtBalance, Integer> {

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    @Query("SELECT t FROM MtBalance t WHERE t.orderSn =:orderSn ORDER BY t.id DESC")
    List<MtBalance> getBalanceListByOrderSn(@Param("orderSn") String orderSn);
}

