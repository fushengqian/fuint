package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.CommissionLogDto;
import com.fuint.common.param.CommissionLogPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.repository.model.MtCommissionLog;

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
     * @param commissionLogPage
     * @return
     */
    PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(CommissionLogPage commissionLogPage);

    /**
     * 计算订单分销提成
     *
     * @param  orderId 订单ID
     * @return
     */
    void calculateCommission(Integer orderId);

    /**
     * 根据ID获取记录信息
     *
     * @param  id 记录ID
     * @return
     */
    CommissionLogDto queryCommissionLogById(Integer id);

    /**
     * 更新分销提成记录
     *
     * @param requestParam 请求参数
     * @throws BusinessCheckException
     * @return
     */
    void updateCommissionLog(CommissionLogRequest requestParam) throws BusinessCheckException;
}
