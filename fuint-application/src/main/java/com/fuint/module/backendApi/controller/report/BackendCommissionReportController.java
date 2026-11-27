package com.fuint.module.backendApi.controller.report;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.CommissionReportService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分佣提成报表统计controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-分佣提成报表接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionReport")
public class BackendCommissionReportController extends BaseController {

    private CommissionReportService commissionReportService;

    @Operation(summary = "分佣提成概览统计")
    @GetMapping("/getOverview")
    @CrossOrigin
    public ResponseObject getOverview() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        return getSuccessResult(commissionReportService.getOverview(accountInfo.getMerchantId()));
    }

    @Operation(summary = "近一个月每日佣金变化趋势")
    @GetMapping("/getTrend")
    @CrossOrigin
    public ResponseObject getTrend() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        return getSuccessResult(commissionReportService.getTrend(accountInfo.getMerchantId()));
    }

    @Operation(summary = "会员分佣月榜单")
    @GetMapping("/getMemberRank")
    @CrossOrigin
    public ResponseObject getMemberRank(@RequestParam(value = "month", required = false) String month) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        return getSuccessResult(commissionReportService.getMemberRank(accountInfo.getMerchantId(), month));
    }

    @Operation(summary = "员工提成月榜单")
    @GetMapping("/getStaffRank")
    @CrossOrigin
    public ResponseObject getStaffRank(@RequestParam(value = "month", required = false) String month) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        return getSuccessResult(commissionReportService.getStaffRank(accountInfo.getMerchantId(), month));
    }
}
