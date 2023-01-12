package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtStaff;

import java.util.List;
import java.util.Map;

/**
 * 店铺员工业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface StaffService {

    /**
     * 员工查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtStaff> queryStaffListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存员工信息
     *
     * @param reqStaff
     * @throws BusinessCheckException
     */
    MtStaff saveStaff(MtStaff reqStaff) throws BusinessCheckException;

    /**
     * 根据ID获取店铺信息
     *
     * @param id 员工id
     * @throws BusinessCheckException
     */
    MtStaff queryStaffById(Integer id) throws BusinessCheckException;

    /**
     * 审核更改状态(禁用，审核通过)
     *
     * @param id
     * @throws BusinessCheckException
     */
    Integer updateAuditedStatus(Integer id, String statusEnum) throws BusinessCheckException;

    /**
     * 根据条件搜索员工
     * */
    List<MtStaff> queryStaffByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据手机号获取员工信息
     *
     * @param mobile 手机
     * @throws BusinessCheckException
     */
    MtStaff queryStaffByMobile(String mobile) throws BusinessCheckException;

    /**
     * 根据会员ID获取员工信息
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     */
    MtStaff queryStaffByUserId(Integer userId) throws BusinessCheckException;
}
