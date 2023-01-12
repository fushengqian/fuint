package com.fuint.repository.mapper;

import com.fuint.repository.model.MtOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtOrderMapper extends BaseMapper<MtOrder> {

    BigDecimal getOrderCount();

    BigDecimal getStoreOrderCount(@Param("storeId") Integer storeId);

    BigDecimal getOrderCountByTime(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    BigDecimal getStoreOrderCountByTime(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    MtOrder findByOrderSn(@Param("orderSn") String orderSn);

    BigDecimal getPayMoney();

    BigDecimal getPayMoneyByTime(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    BigDecimal getStorePayMoneyByTime(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    BigDecimal getStorePayMoney(@Param("storeId") Integer storeId);

    Integer getPayUserCount();

    Integer getStorePayUserCount(@Param("storeId") Integer storeId);
}
