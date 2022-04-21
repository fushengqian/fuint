package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtCart;
import org.springframework.transaction.annotation.Transactional;

/**
 * mt_cart Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtCartRepository extends BaseRepository<MtCart, Integer> {
    /**
     * 删除购物车项
     *
     * @return
     */
    @Transactional
    @Modifying
    @Query(value = "delete from MtCart p where p.userId = :userId and p.goodsId = :goodsId and p.skuId = :skuId")
    void deleteCartItem(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId, @Param("skuId") Integer skuId);

    /**
     * 清空购物车项
     *
     * @return
     */
    @Transactional
    @Modifying
    @Query(value = "delete from MtCart p where p.userId = :userId")
    void clearCart(@Param("userId") Integer userId);
}

