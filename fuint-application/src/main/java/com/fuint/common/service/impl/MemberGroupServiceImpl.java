package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.member.MemberGroupDto;
import com.fuint.common.dto.member.UserGroupDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.MemberGroupPage;
import com.fuint.common.service.MemberGroupService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtUserGroupMapper;
import com.fuint.repository.model.MtCouponGroup;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUserGroup;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 会员分组业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class MemberGroupServiceImpl extends ServiceImpl<MtUserGroupMapper, MtUserGroup> implements MemberGroupService {

    private static final Logger logger = LoggerFactory.getLogger(CouponGroupServiceImpl.class);

    private MtUserGroupMapper mtUserGroupMapper;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 分页查询会员分组列表
     *
     * @param memberGroupPage
     * @return
     */
    @Override
    public PaginationResponse<UserGroupDto> queryMemberGroupListByPagination(MemberGroupPage memberGroupPage) {
        Page<MtCouponGroup> pageHelper = PageHelper.startPage(memberGroupPage.getPage(), memberGroupPage.getPageSize());
        LambdaQueryWrapper<MtUserGroup> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserGroup::getStatus, StatusEnum.DISABLE.getKey());
        lambdaQueryWrapper.eq(MtUserGroup::getParentId, 0);
        String name = memberGroupPage.getName();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtUserGroup::getName, name);
        }
        String status = memberGroupPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtUserGroup::getStatus, status);
        }
        Integer id = memberGroupPage.getId();
        if (id != null) {
            lambdaQueryWrapper.eq(MtUserGroup::getId, id);
        }
        Integer merchantId = memberGroupPage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtUserGroup::getMerchantId, merchantId);
        }
        Integer storeId = memberGroupPage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtUserGroup::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtUserGroup::getId);
        List<MtUserGroup> dataList = mtUserGroupMapper.selectList(lambdaQueryWrapper);
        List<UserGroupDto> userGroupList = new ArrayList<>();
        if (dataList != null && dataList.size() > 0) {
            for (MtUserGroup mtUserGroup : dataList) {
                 UserGroupDto userGroupDto = new UserGroupDto();
                 BeanUtils.copyProperties(mtUserGroup, userGroupDto);
                 userGroupDto.setChildren(getChildren(mtUserGroup.getId()));
                 userGroupDto.setMemberNum(getMemberNum(mtUserGroup.getId()));
                 userGroupList.add(userGroupDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(memberGroupPage.getPage(), memberGroupPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<UserGroupDto> paginationResponse = new PaginationResponse(pageImpl, UserGroupDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(userGroupList);

        return paginationResponse;
    }

    /**
     * 添加会员分组
     *
     * @param  memberGroupDto 会员分组
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增会员分组")
    public MtUserGroup addMemberGroup(MemberGroupDto memberGroupDto) {
        MtUserGroup userGroup = new MtUserGroup();
        Integer storeId = memberGroupDto.getStoreId() == null ? 0 : memberGroupDto.getStoreId();
        if (memberGroupDto.getMerchantId() == null || memberGroupDto.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                memberGroupDto.setMerchantId(mtStore.getMerchantId());
            }
        }
        userGroup.setMerchantId(memberGroupDto.getMerchantId());
        userGroup.setStoreId(storeId);
        userGroup.setParentId(memberGroupDto.getParentId());
        userGroup.setName(CommonUtil.replaceXSS(memberGroupDto.getName()));
        userGroup.setDescription(CommonUtil.replaceXSS(memberGroupDto.getDescription()));
        userGroup.setStatus(StatusEnum.ENABLED.getKey());
        userGroup.setCreateTime(new Date());
        userGroup.setUpdateTime(new Date());
        userGroup.setOperator(memberGroupDto.getOperator());
        this.save(userGroup);
        return userGroup;
    }

    /**
     * 根据分组ID获取分组信息
     *
     * @param  id 分组ID
     * @return
     */
    @Override
    public MtUserGroup queryMemberGroupById(Integer id) {
        return mtUserGroupMapper.selectById(id);
    }

    /**
     * 根据ID删除会员分组
     *
     * @param  id 分组ID
     * @param  accountInfo 操作人
     * @return
     */
    @Override
    @OperationServiceLog(description = "删除会员分组")
    public void deleteMemberGroup(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtUserGroup userGroup = queryMemberGroupById(id);
        if (null == userGroup) {
            throw new BusinessCheckException("该分组不存在");
        }
        if (accountInfo.getMerchantId() != null && !accountInfo.getMerchantId().equals(userGroup.getMerchantId())) {
            throw new BusinessCheckException("不同商户，无操作权限");
        }
        userGroup.setStatus(StatusEnum.DISABLE.getKey());
        userGroup.setUpdateTime(new Date());
        userGroup.setOperator(accountInfo.getAccountName());

        this.updateById(userGroup);
    }

    /**
     * 修改会员分组
     *
     * @param  memberGroupDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新会员分组")
    public MtUserGroup updateMemberGroup(MemberGroupDto memberGroupDto, AccountInfo accountInfo) throws BusinessCheckException {
        MtUserGroup userGroup = queryMemberGroupById(memberGroupDto.getId());
        if (null == userGroup || StatusEnum.DISABLE.getKey().equalsIgnoreCase(userGroup.getStatus())) {
            logger.error("该分组不存在或已被删除");
            throw new BusinessCheckException("该分组不存在或已被删除");
        }
        if (accountInfo.getMerchantId() != null && !accountInfo.getMerchantId().equals(userGroup.getMerchantId())) {
            throw new BusinessCheckException("不同商户，无操作权限");
        }
        if (memberGroupDto.getName() != null) {
            userGroup.setName(CommonUtil.replaceXSS(memberGroupDto.getName()));
        }
        if (memberGroupDto.getDescription() != null) {
            userGroup.setDescription(CommonUtil.replaceXSS(memberGroupDto.getDescription()));
        }
        if (memberGroupDto.getStatus() != null) {
            userGroup.setStatus(memberGroupDto.getStatus());
        }

        userGroup.setUpdateTime(new Date());
        userGroup.setOperator(memberGroupDto.getOperator());
        this.updateById(userGroup);
        return userGroup;
    }

    /**
     * 获取会员分组子类
     *
     * @param groupId 分组ID
     * @return
     * */
    public List<UserGroupDto> getChildren(Integer groupId) {
        Map<String, Object> param = new HashMap<>();
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        param.put("PARENT_ID", groupId);
        List<MtUserGroup> dataList = mtUserGroupMapper.selectByMap(param);
        List<UserGroupDto> children = new ArrayList<>();
        if (dataList != null && dataList.size() > 0) {
            for (MtUserGroup userGroup : dataList) {
                 UserGroupDto userGroupDto = new UserGroupDto();
                 BeanUtils.copyProperties(userGroup, userGroupDto);
                 userGroupDto.setChildren(getChildren(userGroup.getId()));
                 userGroupDto.setMemberNum(getMemberNum(userGroup.getId()));
                 children.add(userGroupDto);
            }
        }
        return children;
    }

    /**
     * 获取分组会员数量
     *
     * @param groupId 分组ID
     * @return
     * */
    public Long getMemberNum(Integer groupId) {
        List<Integer> groupIds = getGroupIds(groupId);
        return mtUserGroupMapper.getMemberNum(groupIds);
    }

    /**
     * 获取会员分组子类ID
     *
     * @param groupId 分组ID
     * @return
     * */
    public List<Integer> getGroupIds(Integer groupId) {
        Map<String, Object> param = new HashMap<>();
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        param.put("PARENT_ID", groupId);
        List<MtUserGroup> dataList = mtUserGroupMapper.selectByMap(param);
        List<Integer> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        if (dataList != null && dataList.size() > 0) {
            for (MtUserGroup userGroup : dataList) {
                 groupIds.add(userGroup.getId());
                 List<Integer> childrenIds = getGroupIds(userGroup.getId());
                 if (childrenIds.size() > 0) {
                     groupIds.addAll(childrenIds);
                 }
            }
        }
        return groupIds;
    }
}
