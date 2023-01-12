package com.fuint.repository.mapper;

import com.fuint.repository.model.TSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 后台菜单 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface TSourceMapper extends BaseMapper<TSource> {

    List<TSource> findSourcesByAccountId(@Param("accountId") Integer accountId);

    List<TSource> findByIdIn(@Param("ids") List<String> ids);

    List<TSource> findByStatus(String var1);

}
