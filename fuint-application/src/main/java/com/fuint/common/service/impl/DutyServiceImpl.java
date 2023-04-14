package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.DutyService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.common.domain.TreeNode;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.TDutyMapper;
import com.fuint.repository.mapper.TDutySourceMapper;
import com.fuint.repository.model.TDuty;
import com.fuint.repository.model.TDutySource;
import com.fuint.repository.model.TSource;
import com.fuint.utils.ArrayUtil;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class DutyServiceImpl extends ServiceImpl<TDutyMapper, TDuty> implements DutyService {

    @Resource
    private TDutyMapper tDutyMapper;

    @Resource
    private TDutySourceMapper tDutySourceMapper;

    @Override
    public List<TDuty> getAvailableRoles() {
        List<TDuty> result = tDutyMapper.findByStatus(StatusEnum.ENABLED.getKey());
        return result;
    }

    @Override
    public TDuty getRoleById(Long roleId) {
        TDuty htDuty = tDutyMapper.selectById(roleId);
        return htDuty;
    }

    /**
     * 根据ID数组获取角色集合
     *
     * @param ids
     * @return
     */
    @Override
    public List<TDuty> findDatasByIds(String[] ids) {
        Long[] arrays = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
             arrays[i] = Long.parseLong(ids[i]);
        }
        return tDutyMapper.findByIdIn(ArrayUtil.toList(arrays));
    }

    /**
     * 删除方法
     *
     * @param dutyId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除后台角色")
    public void deleteDuty(long dutyId) {
        tDutySourceMapper.deleteSourcesByDutyId((int)dutyId);
        tDutyMapper.deleteById(dutyId);
    }

    /**
     * 修改角色
     *
     * @param tduty
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新后台角色")
    public void updateDuty(TDuty tduty, List<TSource> sources) throws BusinessCheckException {
        TDuty existsDuty = this.tDutyMapper.selectById(tduty.getDutyId());
        if (existsDuty == null) {
            throw new BusinessCheckException("角色不存在.");
        }
        if (!StringUtil.equals(tduty.getDutyName(), existsDuty.getDutyName())) {
            TDuty tDuty = this.findByName(tduty.getDutyName());
            if (tDuty != null) {
                throw new BusinessCheckException("角色名已存在.");
            }
        }

        existsDuty.setDescription(tduty.getDescription());
        existsDuty.setDutyType(tduty.getDutyType());
        existsDuty.setDutyName(tduty.getDutyName());
        existsDuty.setStatus(tduty.getStatus());

        if (sources != null && sources.size() > 0) {
            tDutySourceMapper.deleteSourcesByDutyId(tduty.getDutyId());
            for (TSource tSource : sources) {
                 TDutySource dutySource = new TDutySource();
                 dutySource.setDutyId(tduty.getDutyId());
                 dutySource.setSourceId(tSource.getSourceId());
                 tDutySourceMapper.insert(dutySource);
            }
        }

        tDutyMapper.updateById(existsDuty);
    }

    /**
     * 根据角色名称合状态查询角色
     *
     * @param name
     * @return
     */
    @Override
    public TDuty findByName(String name) {
        return this.tDutyMapper.findByName(name);
    }

    /**
     * 根据角色名称获取已经分配的菜单ID集合
     *
     * @param dutyId
     * @return
     */
    @Override
    public List<Long> getSourceIdsByDutyId(Integer dutyId) {
        return tDutySourceMapper.findSourceIdsByDutyId(dutyId);
    }

    /**
     * 角色保存方法
     *
     * @param duty
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增后台角色")
    public void saveDuty(TDuty duty, List<TSource> sources) throws BusinessCheckException {
        TDuty existsDuty = tDutyMapper.findByName(duty.getDutyName());
        if (existsDuty != null) {
            throw new BusinessCheckException("角色名称已经存在.");
        }
        this.tDutyMapper.insert(duty);
        if (sources != null && sources.size() > 0) {
            for (TSource tSource : sources) {
                 TDutySource dutySource = new TDutySource();
                 dutySource.setDutyId(duty.getDutyId());
                 dutySource.setSourceId(tSource.getSourceId());
                 tDutySourceMapper.insert(dutySource);
            }
        }
    }

    /**
     * 分页查询后台角色
     * @param paginationRequest
     * */
    @Override
    public PaginationResponse<TDuty> findDutiesByPagination(PaginationRequest paginationRequest) {
        Page<TDuty> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<TDuty> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(TDuty::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(TDuty::getDutyName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(TDuty::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(TDuty::getDutyId);
        List<TDuty> dataList = tDutyMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<TDuty> paginationResponse = new PaginationResponse(pageImpl, TDuty.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 获取菜单的属性结构
     *
     * @return
     */
    @Override
    public List<TreeNode> getDutyTree() {
        List<TDuty> tDuties = this.getAvailableRoles();
        List<TreeNode> trees = new ArrayList<TreeNode>();
        if (tDuties != null && tDuties.size() > 0) {
            TreeNode sourceTreeNode;
            for (TDuty tDuty : tDuties) {
                 sourceTreeNode = new TreeNode();
                 sourceTreeNode.setName(tDuty.getDutyName());
                 sourceTreeNode.setId(tDuty.getDutyId());
                 sourceTreeNode.setLevel(1);
                 sourceTreeNode.setpId(0);
                 trees.add(sourceTreeNode);
            }
        }
        return trees;
    }

    /**
     * 根据账户获取角色
     *
     * @param accountId
     * @return
     */
    @Override
    public List<Long> findDutiesByAccountId(Integer accountId) {
        return tDutyMapper.getRoleIdsByAccountId(accountId);
    }
}
