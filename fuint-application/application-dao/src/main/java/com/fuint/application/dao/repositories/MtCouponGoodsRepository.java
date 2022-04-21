package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtCouponGoods;
import java.util.List;

/**
* mt_coupon_goods Repository
* Created by FSQ
* Contact wx fsq_better
* Site https://www.fuint.cn
*/ 
@Repository 
public interface MtCouponGoodsRepository extends BaseRepository<MtCouponGoods, Integer> {
    /**
     * 查询卡券适用商品列表
     *
     * @param couponId
     * @return
     */
    @Query("SELECT t FROM MtCouponGoods t WHERE t.couponId =:couponId AND t.status = 'A'")
    List<MtCouponGoods> getCouponGoods(@Param("couponId") Integer couponId);
}

