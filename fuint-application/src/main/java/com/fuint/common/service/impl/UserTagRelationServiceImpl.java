package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.repository.mapper.MtUserTagRelationMapper;
import com.fuint.repository.model.MtUserTagRelation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员标签关联服务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class UserTagRelationServiceImpl extends ServiceImpl<MtUserTagRelationMapper, MtUserTagRelation> implements UserTagRelationService {

    private MtUserTagRelationMapper mtUserTagRelationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserTags(Integer userId, List<Integer> tagIds, String operator) {
        // 先删除原有标签
        mtUserTagRelationMapper.removeTagsByUserId(userId);

        // 添加新标签
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Integer tagId : tagIds) {
                MtUserTagRelation relation = new MtUserTagRelation();
                relation.setUserId(userId);
                relation.setTagId(tagId);
                relation.setOperator(operator);
                relation.setCreateTime(new Date());
                mtUserTagRelationMapper.insert(relation);
            }
        }
    }

    @Override
    public List<Integer> getTagIdsByUserId(Integer userId) {
        return mtUserTagRelationMapper.getTagIdsByUserId(userId);
    }

    @Override
    public List<Integer> getUserIdsByTagId(Integer tagId) {
        return mtUserTagRelationMapper.getUserIdsByTagId(tagId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSetUserTags(List<Integer> userIds, List<Integer> tagIds, String operator) {
        if (userIds == null || userIds.isEmpty() || tagIds == null || tagIds.isEmpty()) {
            return;
        }

        for (Integer userId : userIds) {
            // 获取会员已有标签
            List<Integer> existTagIds = mtUserTagRelationMapper.getTagIdsByUserId(userId);
            List<Integer> newTagIds = new ArrayList<>(tagIds);

            // 合并标签（去重）
            for (Integer existTagId : existTagIds) {
                if (!newTagIds.contains(existTagId)) {
                    newTagIds.add(existTagId);
                }
            }

            // 重新设置标签
            setUserTags(userId, newTagIds, operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagsByUserId(Integer userId) {
        mtUserTagRelationMapper.removeTagsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByTagId(Integer tagId) {
        mtUserTagRelationMapper.removeByTagId(tagId);
    }
}
