package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.DutyStatusRequest;
import com.fuint.repository.model.TDuty;
import com.fuint.repository.model.TSource;
import com.fuint.common.domain.TreeNode;
import java.util.List;

/**
 * 角色服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface DutyService extends IService<TDuty> {
    
    /**
     * 角色保存方法
     *
     * @param duty
     */
    void saveDuty(TDuty duty, List<TSource> sources) throws BusinessCheckException;

    /**
     * 获取有效的角色集合
     *
     * @param merchantId 商户ID
     * @return
     */
    List<TDuty> getAvailableRoles(Integer merchantId);

    /**
     * 根据ID获取角色实体
     *
     * @param roleId
     * @return
     */
    TDuty getRoleById(Long roleId);

    /**
     * 角色信息分页查询
     *
     * @param paginationRequest 分页查询请求对象
     * @return 分页查询结果对象
     */
    PaginationResponse<TDuty> findDutiesByPagination(PaginationRequest paginationRequest);

    /**
     * 根据ID数组获取角色集合
     *
     * @param ids
     * @return
     */
    List<TDuty> findDatasByIds(String[] ids);

    /**
     * 删除方法
     *
     * @param merchantId
     * @param dutyId
     */
    void deleteDuty(Integer merchantId, long dutyId);

    /**
     * 更新状态
     *
     * @param merchantId
     * @param dutyStatusRequest
     * @return
     */
    void updateStatus(Integer merchantId, DutyStatusRequest dutyStatusRequest) throws BusinessCheckException;

    /**
     * 修改角色
     *
     * @param tduty
     * @return
     */
    void updateDuty(TDuty tduty, List<TSource> sources) throws BusinessCheckException;

    /**
     * 根据角色名称合状态查询角色
     *
     * @param merchantId
     * @param name
     * @return
     */
    TDuty findByName(Integer merchantId, String name);

    /**
     * 根据角色名称获取已经分配的菜单ID集合
     *
     * @param dutyId
     * @return
     */
    List<Long> getSourceIdsByDutyId(Integer dutyId);

    /**
     * 获取角色的树形结构
     *
     * @param merchantId
     * @return
     */
    List<TreeNode> getDutyTree(Integer merchantId);

    /**
     * 根据账户获取角色
     *
     * @param accountId
     * @return
     */
    List<Long> findDutiesByAccountId(Integer accountId);
}
