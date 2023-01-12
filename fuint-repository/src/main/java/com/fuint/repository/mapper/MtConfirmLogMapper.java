package com.fuint.repository.mapper;

import com.fuint.repository.model.MtConfirmLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 核销记录表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtConfirmLogMapper extends BaseMapper<MtConfirmLog> {

    Long getConfirmNum(@Param("userCouponId") Integer userCouponId);

    Long getConfirmLogCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    List<MtConfirmLog> getOrderConfirmLogList(@Param("orderId") Integer orderId);

}
