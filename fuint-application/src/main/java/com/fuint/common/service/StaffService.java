package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.merchant.StaffDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.StaffPage;
import com.fuint.framework.exception.BusinessCheckException;
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
public interface StaffService extends IService<MtStaff> {

    /**
     * 员工查询列表
     *
     * @param staffPage
     * @return
     */
    PaginationResponse<StaffDto> queryStaffListByPagination(StaffPage staffPage);

    /**
     * 保存员工信息
     *
     * @param reqStaff 员工信息
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    MtStaff saveStaff(MtStaff reqStaff, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据ID获取店铺信息
     *
     * @param  id 员工id
     * @return
     */
    MtStaff queryStaffById(Integer id);

    /**
     * 审核更改状态(禁用，审核通过)
     *
     * @param staffId 员工ID
     * @param status 状态
     * @param accountInfo 操作人
     * @return
     */
    Integer updateAuditedStatus(Integer staffId, String status, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据条件搜索员工
     *
     * @param params 请求参数
     * @return
     * */
    List<MtStaff> queryStaffByParams(Map<String, Object> params);

    /**
     * 根据手机号获取员工信息
     *
     * @param  mobile 手机
     * @return
     */
    MtStaff queryStaffByMobile(String mobile);

    /**
     * 根据会员ID获取员工信息
     *
     * @param userId 会员ID
     * @return
     */
    MtStaff queryStaffByUserId(Integer userId);

    /**
     * 根据手机号获取员工信息
     *
     * @param  mobile 手机
     * @return
     */
    StaffDto getStaffInfoByMobile(String mobile);
}
