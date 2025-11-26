package com.fuint.repository.mapper;

import com.fuint.repository.model.MtInvoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtInvoiceMapper extends BaseMapper<MtInvoice> {

    BigDecimal getInvoiceTotalAmount(@Param("merchantId") Integer merchantId, @Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

}
