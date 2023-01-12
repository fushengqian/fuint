package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtUserAction;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 会员行为 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserActionMapper extends BaseMapper<MtUserAction> {

    Long getActiveUserCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    Long getStoreActiveUserCount(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

}
