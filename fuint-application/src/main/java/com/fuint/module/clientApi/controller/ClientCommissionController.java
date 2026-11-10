package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.commission.CommissionLogDto;
import com.fuint.common.dto.commission.CommissionOverviewDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.param.CommissionLogPage;
import com.fuint.common.param.WithdrawParam;
import com.fuint.common.service.CommissionCashService;
import com.fuint.common.service.CommissionLogService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 分佣提成接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-店铺相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/commission")
public class ClientCommissionController extends BaseController {

    /**
     * 分销提成记录服务接口
     * */
    private CommissionLogService commissionLogService;

    /**
     * 分销提成提现业务接口
     */
    private CommissionCashService commissionCashService;

    /**
     * 佣金概览
     */
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject overview() {
        UserInfo userInfo = TokenUtil.getUserInfo();
        CommissionOverviewDto overviewDto = commissionLogService.getCommissionOverview(userInfo.getId());
        return getSuccessResult(overviewDto);
    }

    /**
     * 佣金订单
     */
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject orders(@ModelAttribute CommissionLogPage commissionLogPage) {
        UserInfo userInfo = TokenUtil.getUserInfo();
        commissionLogPage.setUserId(userInfo.getId());
        PaginationResponse<CommissionLogDto> paginationResponse = commissionLogService.queryCommissionLogByPagination(commissionLogPage);
        return getSuccessResult(paginationResponse);
    }

    /**
     * 申请提现
     */
    @Operation(summary="申请提现")
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject withdraw(@RequestBody WithdrawParam withdrawParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        withdrawParam.setUserId(userInfo.getId());
        Boolean result = commissionCashService.withdraw(withdrawParam);
        return getSuccessResult(result);
    }

    /**
     * 推荐奖励首页数据（活动规则 + 已邀请人数 + 已获券/积分）
     */
    @Operation(summary="推荐奖励首页数据")
    @RequestMapping(value = "/rewards", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject rewards() {
        UserInfo userInfo = TokenUtil.getUserInfo();
        return getSuccessResult(commissionLogService.getRewardSummary(userInfo.getId()));
    }

    /**
     * 推荐奖励记录（注册奖励 + 交易奖励）
     */
    @Operation(summary="推荐奖励记录")
    @RequestMapping(value = "/rewardLogs", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject rewardLogs(@ModelAttribute CommissionLogPage commissionLogPage) {
        UserInfo userInfo = TokenUtil.getUserInfo();
        commissionLogPage.setUserId(userInfo.getId());
        return getSuccessResult(commissionLogService.queryRewardLogsByPagination(commissionLogPage));
    }
}
