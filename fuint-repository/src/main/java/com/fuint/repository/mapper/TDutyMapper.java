package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.TDuty;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface TDutyMapper extends BaseMapper<TDuty> {

    List<TDuty> findByStatus(@Param("status") String status);

    List<TDuty> findByIdIn(@Param("ids") List<Integer> ids);

    TDuty findByName(@Param("name") String name);

    List<Long> getRoleIdsByAccountId(@Param("accountId") Integer accountId);
}
