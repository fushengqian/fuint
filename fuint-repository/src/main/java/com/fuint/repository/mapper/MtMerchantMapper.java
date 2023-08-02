package com.fuint.repository.mapper;

import com.fuint.repository.model.MtMerchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商户表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtMerchantMapper extends BaseMapper<MtMerchant> {

    MtMerchant queryMerchantByName(@Param("name") String name);

    MtMerchant queryMerchantByNo(@Param("merchantNo") String merchantNo);

}
