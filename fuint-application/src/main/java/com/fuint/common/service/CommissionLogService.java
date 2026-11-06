package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.commission.CommissionLogDto;
import com.fuint.common.dto.commission.CommissionOverviewDto;
import com.fuint.common.param.CommissionLogPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.repository.model.MtCommissionLog;

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
     * @param commissionLogPage
     * @return
     */
    PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(CommissionLogPage commissionLogPage);

    /**
     * 获取佣金概览数据
     *
     * @param userId 会员ID
     * @return
     */
    CommissionOverviewDto getCommissionOverview(Integer userId);

    /**
     * 计算订单分销提成
     *
     * @param  orderId 订单ID
     * @throws BusinessCheckException
     * @return
     */
    void calculateCommission(Integer orderId) throws BusinessCheckException;

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

    /**
     * 被推荐人注册后，给一级邀请人发放注册奖励（券/积分）
     *
     * @param inviteeUserId 被推荐人会员ID
     * @return
     */
    void grantRegisterReward(Integer inviteeUserId);

    /**
     * 订单支付成功后，给一级邀请人发放交易奖励（券/积分）
     *
     * @param orderId 订单ID
     * @return
     */
    void grantOrderReward(Integer orderId);

    /**
     * 发放单条奖励记录（供定时任务重试）
     *
     * @param logId 奖励记录ID
     * @return
     */
    void grantRewardLog(Integer logId);

    /**
     * 重试待发放/发放失败的推荐奖励记录
     *
     * @return
     */
    void retryRewards();

    /**
     * 退款前校验该订单的推荐奖励是否可追回
     *
     * @param orderId 订单ID
     * @return canRefund / blockers / rewards
     */
    Map<String, Object> checkRewardClawback(Integer orderId);

    /**
     * 退款成功后追回该订单的推荐奖励
     *
     * @param orderId 订单ID
     * @param operator 操作人
     * @return
     */
    void clawbackOrderReward(Integer orderId, String operator);
}
