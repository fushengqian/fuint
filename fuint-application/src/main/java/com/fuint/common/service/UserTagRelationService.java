package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.repository.model.MtUserTagRelation;

import java.util.List;

/**
 * 会员标签关联服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface UserTagRelationService extends IService<MtUserTagRelation> {

    /**
     * 设置会员标签
     *
     * @param userId 会员ID
     * @param tagIds 标签ID列表
     * @param operator 操作人
     * @return
     */
    void setUserTags(Integer userId, List<Integer> tagIds, String operator);

    /**
     * 获取会员的标签ID列表
     *
     * @param userId 会员ID
     * @return
     */
    List<Integer> getTagIdsByUserId(Integer userId);

    /**
     * 获取标签的会员ID列表
     *
     * @param tagId 标签ID
     * @return
     */
    List<Integer> getUserIdsByTagId(Integer tagId);

    /**
     * 批量设置会员标签
     *
     * @param userIds 会员ID列表
     * @param tagIds 标签ID列表
     * @param operator 操作人
     * @return
     */
    void batchSetUserTags(List<Integer> userIds, List<Integer> tagIds, String operator);

    /**
     * 删除会员的所有标签
     *
     * @param userId 会员ID
     */
    void removeTagsByUserId(Integer userId);

    /**
     * 根据标签ID删除关联
     *
     * @param tagId 标签ID
     */
    void removeByTagId(Integer tagId);
}
