package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtCommissionLog;
import org.apache.ibatis.annotations.Param;

/**
 *  佣金记录 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtCommissionLogMapper extends BaseMapper<MtCommissionLog> {

    Boolean confirmCommissionLog(@Param("merchantId") Integer merchantId, @Param("uuid") String uuid, @Param("operator") String operator);

    Boolean cancelCommissionLog(@Param("merchantId") Integer merchantId, @Param("uuid") String uuid, @Param("operator") String operator);

}
