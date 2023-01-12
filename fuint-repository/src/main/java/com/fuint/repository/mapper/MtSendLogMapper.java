package com.fuint.repository.mapper;

import com.fuint.repository.model.MtSendLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 卡券发放记录表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtSendLogMapper extends BaseMapper<MtSendLog> {

    Integer updateForRemove(@Param("uuid") String uuid, @Param("status") String status, @Param("removeSuccessNum") Integer removeSuccessNum, @Param("removeFailNum") Integer removeFailNum);

    Integer updateSingleForRemove(@Param("uuid") String uuid, @Param("status") String status);

}
