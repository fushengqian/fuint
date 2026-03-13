package com.fuint.common.service;

import com.fuint.common.dto.report.DailyCashierReportDto;
import com.fuint.common.dto.report.DailyCateReportDto;
import com.fuint.common.dto.report.DailySalesReportDto;

import java.util.Date;
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
     * @return
     */
    Map<String, Object> getReportOverview(Integer merchantId, Integer storeId, Date startTime, Date endTime);

    /**
     * 获取日销售统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    DailySalesReportDto getDailySalesReport(Integer merchantId, Integer storeId, Date startTime, Date endTime);

    /**
     * 获取日收银统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    DailyCashierReportDto getDailyCashierReport(Integer merchantId, Integer storeId, Date startTime, Date endTime);

    /**
     * 获取日分类统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    DailyCateReportDto getDailyCateReport(Integer merchantId, Integer storeId, Date startTime, Date endTime);

}
