package com.fuint.module.backendApi.controller.report;




import java.io.IOException;
import java.util.stream.Collectors;
import java.util.ArrayList;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
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
@Tag(name="管理端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/report")
public class BackendReportController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendReportController.class);

    /**
     * 报表服务接口
     */
    private ReportService reportService;

    @Operation(summary="获取日销售统计报表")
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

    @Operation(summary="获取日收银统计报表")
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

    @Operation(summary="获取日分类统计报表")
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

    @Operation(summary="导出日销售统计报表")
    @RequestMapping(value = "/exportDailySales", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailySales(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException, IOException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailySalesReportDto reportDto = reportService.getDailySalesReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        // 汇总行 + 表头（EasyExcel 多行表头）
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
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList(summaryTitle));
        head.add(Arrays.asList(summaryValue));
        head.add(Arrays.asList("日期", "店铺", "订单数量", "销售金额", "现金支付", "微信支付", "支付宝支付", "积分支付", "卡券核销", "余额支付"));

        List<List<Object>> rows = reportDto.getDataList().stream()
                .map(item -> Arrays.<Object>asList(
                        objectConvertToString(item.getDateTime()),
                        objectConvertToString(item.getStoreName()),
                        objectConvertToString(item.getOrderCount()),
                        objectConvertToString(item.getSalesAmount()),
                        objectConvertToString(item.getCashAmount()),
                        objectConvertToString(item.getWechatAmount()),
                        objectConvertToString(item.getAliPayAmount()),
                        objectConvertToString(item.getPointAmount()),
                        objectConvertToString(item.getCouponAmount()),
                        objectConvertToString(item.getBalanceAmount())))
                .collect(Collectors.toList());

        ExcelUtil.exportXlsxWithHead(response, "日销售统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm"), "日销售统计", head, rows);
        logger.info("导出日销售统计报表成功...");
    }

    @Operation(summary="导出日收银统计报表")
    @RequestMapping(value = "/exportDailyCashier", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailyCashier(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException, IOException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCashierReportDto reportDto = reportService.getDailyCashierReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        // 汇总行 + 表头（EasyExcel 多行表头）
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
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList(summaryTitle));
        head.add(Arrays.asList(summaryValue));
        head.add(Arrays.asList("日期", "店铺", "收银员", "订单数量", "销售金额", "现金支付", "微信支付", "支付宝支付", "积分支付", "卡券核销", "余额支付"));

        List<List<Object>> rows = reportDto.getDataList().stream()
                .map(item -> Arrays.<Object>asList(
                        objectConvertToString(item.getDateTime()),
                        objectConvertToString(item.getStoreName()),
                        objectConvertToString(item.getStaffName()),
                        objectConvertToString(item.getOrderCount()),
                        objectConvertToString(item.getSalesAmount()),
                        objectConvertToString(item.getCashAmount()),
                        objectConvertToString(item.getWechatAmount()),
                        objectConvertToString(item.getAliPayAmount()),
                        objectConvertToString(item.getPointAmount()),
                        objectConvertToString(item.getCouponAmount()),
                        objectConvertToString(item.getBalanceAmount())))
                .collect(Collectors.toList());

        ExcelUtil.exportXlsxWithHead(response, "日收银统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm"), "日收银统计", head, rows);
        logger.info("导出日收银统计报表成功...");
    }

    @Operation(summary="导出分类统计报表")
    @RequestMapping(value = "/exportDailyCate", method = RequestMethod.GET)
    @CrossOrigin
    public void exportDailyCate(HttpServletResponse response, @ModelAttribute StatisticParam param) throws ParseException, IOException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<Integer> storeIds = param.getStoreIds();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeIds = Arrays.asList(accountInfo.getStoreId());
        }
        DailyCateReportDto reportDto = reportService.getDailyCateReport(accountInfo.getMerchantId(), storeIds, param.getStartTime(), param.getEndTime(), Constants.PAGE_NUMBER, Constants.MAX_ROWS);

        // 汇总行 + 表头（EasyExcel 多行表头）
        String[] summaryTitle = { "总销售数量", "总销售金额", "总购买人数", "总销售成本", "总利润金额", "总退款数量" };
        String[] summaryValue = {
                objectConvertToString(reportDto.getTotalSalesCount()),
                objectConvertToString(reportDto.getTotalSalesAmount()),
                objectConvertToString(reportDto.getTotalBuyerCount()),
                objectConvertToString(reportDto.getTotalCostAmount()),
                objectConvertToString(reportDto.getTotalProfitAmount()),
                objectConvertToString(reportDto.getTotalRefundCount())
        };
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList(summaryTitle));
        head.add(Arrays.asList(summaryValue));
        head.add(Arrays.asList("日期", "店铺", "分类名称", "销售数量", "销售金额", "购买人数", "销售成本", "利润金额", "退款数量"));

        List<List<Object>> rows = reportDto.getDataList().stream()
                .map(item -> Arrays.<Object>asList(
                        objectConvertToString(item.getDateTime()),
                        objectConvertToString(item.getStoreName()),
                        objectConvertToString(item.getCateName()),
                        objectConvertToString(item.getSalesCount()),
                        objectConvertToString(item.getSalesAmount()),
                        objectConvertToString(item.getBuyerCount()),
                        objectConvertToString(item.getCostAmount()),
                        objectConvertToString(item.getProfitAmount()),
                        objectConvertToString(item.getRefundCount())))
                .collect(Collectors.toList());

        ExcelUtil.exportXlsxWithHead(response, "分类统计报表_" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm"), "分类统计", head, rows);
        logger.info("导出分类统计报表成功...");
    }
}
