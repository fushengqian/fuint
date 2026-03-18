package com.fuint.common.service;

import com.fuint.common.dto.report.DailyCashierReportDto;
import com.fuint.common.dto.report.DailyCateReportDto;
import com.fuint.common.dto.report.DailySalesReportDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 会员标签服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ReportService {

    /**
     * 统计报表概述数据
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @throws ParseException
     * @return
     */
    Map<String, Object> getReportOverview(Integer merchantId, Integer storeId, String startTime, String endTime) throws ParseException;

    /**
     * 获取日销售统计报表
     *
     * @param merchantId 商户ID
     * @param storeIds 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param pageSize 每页数量
     * @throws ParseException
     * @return
     */
    DailySalesReportDto getDailySalesReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException;

    /**
     * 获取日收银统计报表
     *
     * @param merchantId 商户ID
     * @param storeIds 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param pageSize 每页数量
     * @throws ParseException
     * @return
     */
    DailyCashierReportDto getDailyCashierReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException;

    /**
     * 获取日分类统计报表
     *
     * @param merchantId 商户ID
     * @param storeIds 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 当前页数
     * @param pageSize 每页数量
     * @throws ParseException
     * @return
     */
    DailyCateReportDto getDailyCateReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException;

}
