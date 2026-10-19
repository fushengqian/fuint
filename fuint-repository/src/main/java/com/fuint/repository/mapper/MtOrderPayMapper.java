package com.fuint.repository.mapper;

import com.fuint.repository.model.MtOrderPay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单支付单表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtOrderPayMapper extends BaseMapper<MtOrderPay> {

    @Select("select * from mt_order_pay where PAY_SN = #{paySn} limit 1")
    MtOrderPay findByPaySn(@Param("paySn") String paySn);
}
