package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtCommissionRelation;
import org.apache.ibatis.annotations.Param;

/**
 * 会员分销关系 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtCommissionRelationMapper extends BaseMapper<MtCommissionRelation> {

    Integer getCommissionUserId(@Param("merchantId") Integer merchantId, @Param("userId") Integer userId);

}
