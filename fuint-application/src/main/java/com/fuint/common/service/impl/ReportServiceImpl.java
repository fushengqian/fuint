package com.fuint.common.service.impl;

import com.fuint.common.dto.report.DailyCashierReportDto;
import com.fuint.common.dto.report.DailyCateReportDto;
import com.fuint.common.dto.report.DailySalesReportDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.ReportService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.DateUtil;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员标签服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 统计报表概述数据
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    @Override
    public Map<String, Object> getReportOverview(Integer merchantId, Integer storeId, String begin, String end) throws ParseException {
        Date startTime = StringUtil.isNotEmpty(begin) ? DateUtil.parseDate(begin) : null;
        Date endTime = StringUtil.isNotEmpty(end) ? DateUtil.parseDate(end) : null;

        Long totalUserCount = memberService.getUserCount(merchantId, storeId);

        // 新增会员数量
        Long userCount = memberService.getUserCount(merchantId, storeId, startTime, endTime);

        // 总订单数
        BigDecimal totalOrderCount = orderService.getOrderCount(merchantId, storeId);
        // 订单数
        BigDecimal orderCount = orderService.getOrderCount(merchantId, storeId, startTime, endTime);

        // 交易金额
        BigDecimal payAmount = orderService.getPayMoney(merchantId, storeId, startTime, endTime);
        // 总交易金额
        BigDecimal totalPayAmount = orderService.getPayMoney(merchantId, storeId);

        // 活跃会员数
        Long activeUserCount = memberService.getActiveUserCount(merchantId, storeId, startTime, endTime);

        // 总支付人数
        Integer totalPayUserCount = orderService.getPayUserCount(merchantId, storeId);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(merchantId, storeId, StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userCount);
        result.put("totalUserCount", totalUserCount);
        result.put("orderCount", orderCount);
        result.put("totalOrderCount", totalOrderCount);
        result.put("payAmount", payAmount);
        result.put("totalPayAmount", totalPayAmount);
        result.put("activeUserCount", activeUserCount);
        result.put("totalPayUserCount", totalPayUserCount);
        result.put("storeList", storeList);

        return result;
    }

    /**
     * 获取日销售统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Override
    public DailySalesReportDto getDailySalesReport(Integer merchantId, Integer storeId, String startTime, String endTime) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailySalesReportDto dailySalesReportDto = new DailySalesReportDto();
        return dailySalesReportDto;
    }

    /**
     * 获取日收银统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Override
    public DailyCashierReportDto getDailyCashierReport(Integer merchantId, Integer storeId, String startTime, String endTime) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCashierReportDto dailyCashierReportDto = new DailyCashierReportDto();
        return dailyCashierReportDto;
    }

    /**
     * 获取日分类统计报表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Override
    public DailyCateReportDto getDailyCateReport(Integer merchantId, Integer storeId, String startTime, String endTime) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCateReportDto dailyCateReportDto = new DailyCateReportDto();
        return dailyCateReportDto;
    }
}
