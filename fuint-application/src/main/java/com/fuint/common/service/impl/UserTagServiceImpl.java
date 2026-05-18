package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.member.UserTagDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.common.service.UserTagService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.mapper.MtUserTagMapper;
import com.fuint.repository.model.MtUserTag;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员标签服务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class UserTagServiceImpl extends ServiceImpl<MtUserTagMapper, MtUserTag> implements UserTagService {

    private MtUserTagMapper mtUserTagMapper;

    private UserTagRelationService userTagRelationService;

    @Override
    public List<MtUserTag> getMerchantTagList(Integer merchantId, String status) {
        return mtUserTagMapper.getMerchantTagList(merchantId, status);
    }

    @Override
    public Map<Integer, List<UserTagDto>> getUserTagsByUserIds(Integer merchantId, List<Integer> userIds) {
        Map<Integer, List<UserTagDto>> result = new java.util.HashMap<>();

        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        // 初始化每个会员的标签列表
        for (Integer userId : userIds) {
            result.put(userId, new java.util.ArrayList<>());
        }

        // 获取所有标签（启用状态）
        List<MtUserTag> allTags = mtUserTagMapper.getMerchantTagList(null, null);

        // 遍历每个会员，获取其标签
        for (Integer userId : userIds) {
            List<Integer> tagIds = userTagRelationService.getTagIdsByUserId(userId);
            List<UserTagDto> tagList = allTags.stream()
                .filter(tag -> tagIds.contains(tag.getId()))
                .map(tag -> {
                    UserTagDto dto = new UserTagDto();
                    dto.setId(tag.getId());
                    dto.setName(tag.getName());
                    dto.setColor(tag.getColor());
                    dto.setSort(tag.getSort());
                    dto.setDescription(tag.getDescription());
                    dto.setCreateTime(tag.getCreateTime());
                    return dto;
                })
                .collect(Collectors.toList());
            result.put(userId, tagList);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtUserTag addTag(MtUserTag mtUserTag) throws BusinessCheckException {
        // 平台账号没有新增权限
        if (mtUserTag.getMerchantId() == null || mtUserTag.getMerchantId() <= 0) {
            throw new BusinessCheckException("平台账号不能执行该操作");
        }

        // 校验名称是否重复
        MtUserTag existTag = mtUserTagMapper.getTagByName(mtUserTag.getMerchantId(), mtUserTag.getName());
        if (existTag != null) {
            throw new BusinessCheckException("标签名称已存在");
        }

        if (StringUtil.isEmpty(mtUserTag.getColor())) {
            mtUserTag.setColor("#1890ff");
        }
        mtUserTag.setStatus(StatusEnum.ENABLED.getKey());
        mtUserTag.setCreateTime(new Date());
        mtUserTag.setUpdateTime(new Date());

        mtUserTagMapper.insert(mtUserTag);
        return mtUserTag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtUserTag updateTag(MtUserTag mtUserTag, AccountInfo accountInfo) throws BusinessCheckException {
        // 平台账号没有编辑权限
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            throw new BusinessCheckException("抱歉，您没有编辑权限");
        }

        MtUserTag tagInfo = mtUserTagMapper.selectById(mtUserTag.getId());
        if (tagInfo == null) {
            throw new BusinessCheckException("标签不存在");
        }

        // 校验商户权限
        if (!accountInfo.getMerchantId().equals(tagInfo.getMerchantId())) {
            throw new BusinessCheckException("抱歉，您没有编辑权限");
        }

        // 校验名称是否重复
        if (!tagInfo.getName().equals(mtUserTag.getName())) {
            MtUserTag existTag = mtUserTagMapper.getTagByName(tagInfo.getMerchantId(), mtUserTag.getName());
            if (existTag != null) {
                throw new BusinessCheckException("标签名称已存在");
            }
        }

        tagInfo.setName(mtUserTag.getName());
        tagInfo.setColor(mtUserTag.getColor());
        tagInfo.setSort(mtUserTag.getSort());
        tagInfo.setDescription(mtUserTag.getDescription());
        tagInfo.setOperator(mtUserTag.getOperator());
        tagInfo.setUpdateTime(new Date());

        mtUserTagMapper.updateById(tagInfo);
        return tagInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtUserTag tagInfo = mtUserTagMapper.selectById(id);
        if (tagInfo == null) {
            throw new BusinessCheckException("标签不存在");
        }

        Integer merchantId = accountInfo.getMerchantId();
        String operator = accountInfo.getAccountName();

        // 校验商户权限
        if (merchantId != null && merchantId > 0 && !merchantId.equals(tagInfo.getMerchantId())) {
            throw new BusinessCheckException("抱歉，您没有删除权限");
        }

        tagInfo.setStatus(StatusEnum.DISABLE.getKey());
        tagInfo.setOperator(operator);
        tagInfo.setUpdateTime(new Date());
        mtUserTagMapper.updateById(tagInfo);

        // 删除关联关系
        userTagRelationService.removeByTagId(id);
    }

    @Override
    public MtUserTag getTagById(Integer id) {
        return mtUserTagMapper.selectById(id);
    }
}
