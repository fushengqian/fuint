package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtCommissionLog;

import java.util.List;
import java.util.Map;

/**
 * 分销提成记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionLogService extends IService<MtCommissionLog> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCommissionLog> queryDataByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加分销提成记录
     *
     * @param  commissionLog
     * @throws BusinessCheckException
     */
    MtCommissionLog addCommissionLog(MtCommissionLog commissionLog) throws BusinessCheckException;

    /**
     * 根据ID获取记录信息
     *
     * @param  id
     * @throws BusinessCheckException
     */
    MtCommissionLog queryCommissionLogById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除分销提成记录
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCommissionLog(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新数据
     * @param  commissionLog
     * @throws BusinessCheckException
     * */
    MtCommissionLog updateCommissionLog(MtCommissionLog commissionLog) throws BusinessCheckException;

    /**
     * 根据条件搜索分销提成记录
     * */
    List<MtCommissionLog> queryDataByParams(Map<String, Object> params) throws BusinessCheckException;

}
