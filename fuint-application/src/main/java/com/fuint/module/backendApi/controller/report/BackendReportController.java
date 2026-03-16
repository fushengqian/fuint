package com.fuint.module.backendApi.controller.report;

import com.fuint.common.dto.report.DailyCashierReportDto;
import com.fuint.common.dto.report.DailyCateReportDto;
import com.fuint.common.dto.report.DailySalesReportDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.StatisticParam;
import com.fuint.common.service.ReportService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * 后台报表统计controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/report")
public class BackendReportController extends BaseController {

    /**
     * 报表服务接口
     */
    private ReportService reportService;

    @ApiOperation(value = "获取日销售统计报表")
    @RequestMapping(value = "/getDailySalesReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailySalesReport(@ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        DailySalesReportDto reportDto = reportService.getDailySalesReport(accountInfo.getMerchantId(), accountInfo.getStoreId(), param.getStartTime(), param.getEndTime());
        return getSuccessResult(reportDto);
    }

    @ApiOperation(value = "获取日收银统计报表")
    @RequestMapping(value = "/getDailyCashierReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailyCashierReport() throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        DailyCashierReportDto reportDto = reportService.getDailyCashierReport(accountInfo.getMerchantId(), accountInfo.getStoreId(), null, null);
        return getSuccessResult(reportDto);
    }

    @ApiOperation(value = "获取日收银统计报表")
    @RequestMapping(value = "/getDailyCateReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailyCateReport() throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        DailyCateReportDto reportDto = reportService.getDailyCateReport(accountInfo.getMerchantId(), accountInfo.getStoreId(), null, null);
        return getSuccessResult(reportDto);
    }
}
