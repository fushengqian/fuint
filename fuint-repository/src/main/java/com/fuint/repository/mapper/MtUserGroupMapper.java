package com.fuint.repository.mapper;

import com.fuint.repository.model.MtUserGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员分组 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserGroupMapper extends BaseMapper<MtUserGroup> {
    Long getMemberNum(@Param("groupIds") List<Integer> groupIds);
}