package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGoodsSku;
import java.util.List;

/**
 * mt_goods_sku Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtGoodsSkuRepository extends BaseRepository<MtGoodsSku, Integer> {
    /**
     * 根据编码查询
     *
     * @return
     */
    @Query("select t from MtGoodsSku t where t.skuNo = :skuNo")
    List<MtGoodsSku> getBySkuNo(@Param("skuNo") String skuNo);
}

