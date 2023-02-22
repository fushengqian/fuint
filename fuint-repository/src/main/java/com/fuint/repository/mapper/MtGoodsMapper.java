package com.fuint.repository.mapper;

import com.fuint.repository.model.MtGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  商品 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtGoodsMapper extends BaseMapper<MtGoods> {

    List<MtGoods> getStoreGoodsList(@Param("storeId") Integer storeId);

    List<MtGoods> searchStoreGoodsList(@Param("storeId") Integer storeId, @Param("keyword") String keyword);

    MtGoods getByGoodsNo(@Param("goodsNo") String goodsNo);

    Boolean updateInitSale(@Param("goodsId") Integer goodsId);

}
