package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGoods;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * mt_goods Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtGoodsRepository extends BaseRepository<MtGoods, Integer> {

    /**
     * 获取店铺商品列表
     * @param storeId
     * @return
     * */
    @Query("select t from MtGoods t where t.storeId = 0 or t.storeId = :storeId AND t.status = 'A' order by t.sort asc")
    List<MtGoods> getStoreGoodsList(@Param("storeId") Integer storeId);

    /**
     * 根据编码获取商品信息
     * @param goodsNo
     * @return
     * */
    @Query("select t from MtGoods t where t.goodsNo =:goodsNo AND t.status = 'A'")
    MtGoods getByGoodsNo(@Param("goodsNo") String goodsNo);
}

