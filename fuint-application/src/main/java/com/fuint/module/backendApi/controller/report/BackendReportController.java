package com.fuint.module.backendApi.controller.report;

import com.fuint.common.Constants;
import com.fuint.common.dto.report.*;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.StatisticParam;
import com.fuint.common.service.ReportService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.ExcelUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.fuint.common.util.XlsUtil.objectConvertToString;

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

    private static final Logger logger = LoggerFactory.getLogger(BackendReportController.class);

    /**
     * 报表服务接口
     */
    private ReportService reportService;

    @ApiOperation(value = "获取日销售统计报表")
    @RequestMapping(value = "/getDailySalesReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailySalesReport(@ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailySalesReportDto reportDto = reportService.getDailySalesReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), param.getPage(), param.getPageSize());
        return getSuccessResult(reportDto);
    }

    @ApiOperation(value = "获取日收银统计报表")
    @RequestMapping(value = "/getDailyCashierReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailyCashierReport(@ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCashierReportDto reportDto = reportService.getDailyCashierReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), param.getPage(), param.getPageSize());
        return getSuccessResult(reportDto);
    }

    @ApiOperation(value = "获取日分类统计报表")
    @RequestMapping(value = "/getDailyCateReport", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getDailyCateReport(@ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCateReportDto reportDto = reportService.getDailyCateReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), param.getPage(), param.getPageSize());
        return getSuccessResult(reportDto);
    }

    @ApiOperation(value = "导出日销售统计报表")
    @RequestMapping(value = "/exportDailySales", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailySales(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailySalesReportDto reportDto = reportService.getDailySalesReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        String[] title = { "日期", "店铺", "订单数量", "销售金额", "现金支付", "微信支付", "支付宝支付", "积分支付", "卡券核销", "余额支付" };
        String fileName = "日销售统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") + ".xls";
        String sheetName = "日销售统计";

        // 构建顶部汇总行
        String[] summaryTitle = { "总订单笔数", "总销售金额", "总现金支付金额", "总微信支付金额", "总支付宝支付金额", "总积分支付金额", "总卡券核销金额", "总余额支付金额" };
        String[] summaryValue = {
                objectConvertToString(reportDto.getTotalOrderCount()),
                objectConvertToString(reportDto.getTotalSalesAmount()),
                objectConvertToString(reportDto.getTotalCashAmount()),
                objectConvertToString(reportDto.getTotalWechatAmount()),
                objectConvertToString(reportDto.getTotalAliPayAmount()),
                objectConvertToString(reportDto.getTotalPointAmount()),
                objectConvertToString(reportDto.getTotalCouponAmount()),
                objectConvertToString(reportDto.getTotalBalanceAmount())
        };
        String[][] summary = { summaryTitle, summaryValue };

        List<DailySalesItemDto> list = reportDto.getDataList();
        String[][] content = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            DailySalesItemDto item = list.get(i);
            content[i][0] = objectConvertToString(item.getDateTime());
            content[i][1] = objectConvertToString(item.getStoreName());
            content[i][2] = objectConvertToString(item.getOrderCount());
            content[i][3] = objectConvertToString(item.getSalesAmount());
            content[i][4] = objectConvertToString(item.getCashAmount());
            content[i][5] = objectConvertToString(item.getWechatAmount());
            content[i][6] = objectConvertToString(item.getAliPayAmount());
            content[i][7] = objectConvertToString(item.getPointAmount());
            content[i][8] = objectConvertToString(item.getCouponAmount());
            content[i][9] = objectConvertToString(item.getBalanceAmount());
        }

        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null, summary);
        ExcelUtil.setResponseHeader(response, fileName, wb);
        logger.info("导出日销售统计报表成功...");
    }

    @ApiOperation(value = "导出日收银统计报表")
    @RequestMapping(value = "/exportDailyCashier", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailyCashier(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCashierReportDto reportDto = reportService.getDailyCashierReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        String[] title = { "日期", "店铺", "收银员", "订单数量", "销售金额", "现金支付", "微信支付", "支付宝支付", "积分支付", "卡券核销", "余额支付" };
        String fileName = "日收银统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") + ".xls";
        String sheetName = "日收银统计";

        // 构建顶部汇总行
        String[] summaryTitle = { "总订单笔数", "总销售金额", "总现金支付金额", "总微信支付金额", "总支付宝支付金额", "总积分支付金额", "总卡券核销金额", "总余额支付金额" };
        String[] summaryValue = {
                objectConvertToString(reportDto.getTotalOrderCount()),
                objectConvertToString(reportDto.getTotalSalesAmount()),
                objectConvertToString(reportDto.getTotalCashAmount()),
                objectConvertToString(reportDto.getTotalWechatAmount()),
                objectConvertToString(reportDto.getTotalAliPayAmount()),
                objectConvertToString(reportDto.getTotalPointAmount()),
                objectConvertToString(reportDto.getTotalCouponAmount()),
                objectConvertToString(reportDto.getTotalBalanceAmount())
        };
        String[][] summary = { summaryTitle, summaryValue };

        List<DailyCashierItemDto> list = reportDto.getDataList();
        String[][] content = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            DailyCashierItemDto item = list.get(i);
            content[i][0] = objectConvertToString(item.getDateTime());
            content[i][1] = objectConvertToString(item.getStoreName());
            content[i][2] = objectConvertToString(item.getStaffName());
            content[i][3] = objectConvertToString(item.getOrderCount());
            content[i][4] = objectConvertToString(item.getSalesAmount());
            content[i][5] = objectConvertToString(item.getCashAmount());
            content[i][6] = objectConvertToString(item.getWechatAmount());
            content[i][7] = objectConvertToString(item.getAliPayAmount());
            content[i][8] = objectConvertToString(item.getPointAmount());
            content[i][9] = objectConvertToString(item.getCouponAmount());
            content[i][10] = objectConvertToString(item.getBalanceAmount());
        }

        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null, summary);
        ExcelUtil.setResponseHeader(response, fileName, wb);
        logger.info("导出日收银统计报表成功...");
    }

    @ApiOperation(value = "导出分类统计报表")
    @RequestMapping(value = "/exportDailyCate", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailyCate(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCateReportDto reportDto = reportService.getDailyCateReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        String[] title = { "日期", "店铺", "分类名称", "销售数量", "销售金额", "购买人数", "销售成本", "利润金额", "退款数量" };
        String fileName = "分类统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") + ".xls";
        String sheetName = "分类统计";

        // 构建顶部汇总行
        String[] summaryTitle = { "总销售数量", "总销售金额", "总购买人数", "总销售成本", "总利润金额", "总退款数量" };
        String[] summaryValue = {
                objectConvertToString(reportDto.getTotalSalesCount()),
                objectConvertToString(reportDto.getTotalSalesAmount()),
                objectConvertToString(reportDto.getTotalBuyerCount()),
                objectConvertToString(reportDto.getTotalCostAmount()),
                objectConvertToString(reportDto.getTotalProfitAmount()),
                objectConvertToString(reportDto.getTotalRefundCount())
        };
        String[][] summary = { summaryTitle, summaryValue };

        List<DailyCateItemDto> list = reportDto.getDataList();
        String[][] content = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            DailyCateItemDto item = list.get(i);
            content[i][0] = objectConvertToString(item.getDateTime());
            content[i][1] = objectConvertToString(item.getStoreName());
            content[i][2] = objectConvertToString(item.getCateName());
            content[i][3] = objectConvertToString(item.getSalesCount());
            content[i][4] = objectConvertToString(item.getSalesAmount());
            content[i][5] = objectConvertToString(item.getBuyerCount());
            content[i][6] = objectConvertToString(item.getCostAmount());
            content[i][7] = objectConvertToString(item.getProfitAmount());
            content[i][8] = objectConvertToString(item.getRefundCount());
        }

        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null, summary);
        ExcelUtil.setResponseHeader(response, fileName, wb);
        logger.info("导出分类统计报表成功...");
    }
}
