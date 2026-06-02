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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 分佣提成接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-店铺相关接口")
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
    @ApiOperation(value = "申请提现")
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject withdraw(@RequestBody WithdrawParam withdrawParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        withdrawParam.setUserId(userInfo.getId());
        Boolean result = commissionCashService.withdraw(withdrawParam);
        return getSuccessResult(result);
    }
}
