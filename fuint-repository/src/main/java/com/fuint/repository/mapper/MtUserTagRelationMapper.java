package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtUserTagRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员标签关联Mapper接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserTagRelationMapper extends BaseMapper<MtUserTagRelation> {

    /**
     * 获取会员的标签ID列表
     *
     * @param userId 会员ID
     * @return
     */
    List<Integer> getTagIdsByUserId(@Param("userId") Integer userId);

    /**
     * 获取标签的会员ID列表
     *
     * @param tagId 标签ID
     * @return
     */
    List<Integer> getUserIdsByTagId(@Param("tagId") Integer tagId);

    /**
     * 删除会员的所有标签
     *
     * @param userId 会员ID
     * @return
     */
    Boolean removeTagsByUserId(@Param("userId") Integer userId);

    /**
     * 批量删除标签关联
     *
     * @param tagId 标签ID
     * @return
     */
    Boolean removeByTagId(@Param("tagId") Integer tagId);
}
