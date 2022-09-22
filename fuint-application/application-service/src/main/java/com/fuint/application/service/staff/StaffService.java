package com.fuint.application.service.staff;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtStaff;
import java.util.List;
import java.util.Map;

/**
 * 店铺员工管理接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
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
     * @param ids
     * @throws BusinessCheckException
     */
    Integer updateAuditedStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException;

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
