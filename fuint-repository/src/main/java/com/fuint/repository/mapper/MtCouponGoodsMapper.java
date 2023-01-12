package com.fuint.repository.mapper;

import com.fuint.repository.model.MtCouponGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡券商品表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtCouponGoodsMapper extends BaseMapper<MtCouponGoods> {

    List<MtCouponGoods> getCouponGoods(@Param("couponId") Integer couponId);

}
