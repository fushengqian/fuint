package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuint.common.dto.report.DailyCashierReportDto;
import com.fuint.common.dto.report.DailyCateReportDto;
import com.fuint.common.dto.report.DailySalesItemDto;
import com.fuint.common.dto.report.DailySalesReportDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.ReportService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.DateUtil;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

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
     * 订单Mapper
     */
    private MtOrderMapper mtOrderMapper;

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

    @Override
    public DailySalesReportDto getDailySalesReport(Integer merchantId, Integer storeId, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailySalesReportDto dailySalesReportDto = new DailySalesReportDto();

        // 获取店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(merchantId, storeId, StatusEnum.ENABLED.getKey());

        // 构建日期列表
        List<String> dateList = new ArrayList<>();
        if (start != null && end != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(end);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            Date endDate = endCal.getTime();
            while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
                dateList.add(DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd"));
                calendar.add(Calendar.DATE, 1);
            }
        }

        // 如果没有日期范围，默认查询最近7天（过去7天）
        if (dateList.isEmpty()) {
            for (int i = 6; i >= 0; i--) {
                dateList.add(DateUtil.formatDate(DateUtil.getDayBegin(i), "yyyy-MM-dd"));
            }
        }

        List<DailySalesItemDto> allDataList = new ArrayList<>();
        Integer totalOrderCount = 0;
        BigDecimal totalSalesAmount = new BigDecimal("0");
        BigDecimal totalCashAmount = new BigDecimal("0");
        BigDecimal totalWechatAmount = new BigDecimal("0");
        BigDecimal totalAliPayAmount = new BigDecimal("0");
        BigDecimal totalPointAmount = new BigDecimal("0");
        BigDecimal totalCouponAmount = new BigDecimal("0");

        // 遍历每个日期
        for (String dateStr : dateList) {
            Date dayBegin = DateUtil.parseDate(dateStr + " 00:00:00");
            Calendar dayEndCal = Calendar.getInstance();
            dayEndCal.setTime(dayBegin);
            dayEndCal.set(Calendar.HOUR_OF_DAY, 23);
            dayEndCal.set(Calendar.MINUTE, 59);
            dayEndCal.set(Calendar.SECOND, 59);
            dayEndCal.set(Calendar.MILLISECOND, 999);
            Date dayEnd = dayEndCal.getTime();

            // 遍历每个店铺
            for (MtStore store : storeList) {
                DailySalesItemDto itemDto = new DailySalesItemDto();
                itemDto.setDateTime(dateStr);
                itemDto.setStoreId(store.getId());
                itemDto.setStoreName(store.getName());

                // 查询该店铺该日期的订单列表
                LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MtOrder::getStoreId, store.getId());
                wrapper.ge(MtOrder::getCreateTime, dayBegin);
                wrapper.le(MtOrder::getCreateTime, dayEnd);
                wrapper.eq(MtOrder::getPayStatus, "B");
                wrapper.notIn(MtOrder::getStatus, Arrays.asList("C", "G", "H"));

                List<MtOrder> orderList = mtOrderMapper.selectList(wrapper);

                Integer orderCount = orderList.size();
                BigDecimal salesAmount = new BigDecimal("0");
                BigDecimal cashAmount = new BigDecimal("0");
                BigDecimal wechatAmount = new BigDecimal("0");
                BigDecimal aliPayAmount = new BigDecimal("0");
                BigDecimal pointAmount = new BigDecimal("0");
                BigDecimal couponAmount = new BigDecimal("0");

                for (MtOrder order : orderList) {
                    // 销售金额
                    if (order.getPayAmount() != null) {
                        salesAmount = salesAmount.add(order.getPayAmount());
                    }

                    // 积分支付金额
                    if (order.getPointAmount() != null) {
                        pointAmount = pointAmount.add(order.getPointAmount());
                    }

                    // 卡券核销金额（折扣金额）
                    if (order.getDiscount() != null) {
                        couponAmount = couponAmount.add(order.getDiscount());
                    }

                    // 根据支付类型统计
                    String payType = order.getPayType();
                    if (payType != null) {
                        BigDecimal payAmount = order.getPayAmount() != null ? order.getPayAmount() : new BigDecimal("0");
                        switch (payType) {
                            case "CASH":
                                cashAmount = cashAmount.add(payAmount);
                                break;
                            case "JSAPI":
                            case "MICROPAY":
                                wechatAmount = wechatAmount.add(payAmount);
                                break;
                            case "ALISCAN":
                                aliPayAmount = aliPayAmount.add(payAmount);
                                break;
                            default:
                                break;
                        }
                    }
                }

                itemDto.setOrderCount(orderCount);
                itemDto.setSalesAmount(salesAmount);
                itemDto.setCashAmount(cashAmount);
                itemDto.setWechatAmount(wechatAmount);
                itemDto.setAliPayAmount(aliPayAmount);
                itemDto.setPointAmount(pointAmount);
                itemDto.setCouponAmount(couponAmount);

                allDataList.add(itemDto);

                // 累加总计
                totalOrderCount += orderCount;
                totalSalesAmount = totalSalesAmount.add(salesAmount);
                totalCashAmount = totalCashAmount.add(cashAmount);
                totalWechatAmount = totalWechatAmount.add(wechatAmount);
                totalAliPayAmount = totalAliPayAmount.add(aliPayAmount);
                totalPointAmount = totalPointAmount.add(pointAmount);
                totalCouponAmount = totalCouponAmount.add(couponAmount);
            }
        }

        // 按日期倒序排序
        allDataList.sort((a, b) -> {
            int dateCompare = b.getDateTime().compareTo(a.getDateTime());
            if (dateCompare != 0) {
                return dateCompare;
            }
            return b.getStoreId().compareTo(a.getStoreId());
        });

        // 分页处理
        int currentPage = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int total = allDataList.size();
        int startIndex = (currentPage - 1) * size;
        int endIndex = Math.min(startIndex + size, total);
        
        List<DailySalesItemDto> dataList = new ArrayList<>();
        if (startIndex < total) {
            dataList = allDataList.subList(startIndex, endIndex);
        }

        dailySalesReportDto.setTotalOrderCount(totalOrderCount);
        dailySalesReportDto.setTotalSalesAmount(totalSalesAmount);
        dailySalesReportDto.setTotalCashAmount(totalCashAmount);
        dailySalesReportDto.setTotalWechatAmount(totalWechatAmount);
        dailySalesReportDto.setTotalAliPayAmount(totalAliPayAmount);
        dailySalesReportDto.setTotalPointAmount(totalPointAmount);
        dailySalesReportDto.setTotalCouponAmount(totalCouponAmount);
        dailySalesReportDto.setDataList(dataList);
        dailySalesReportDto.setTotal(total);

        return dailySalesReportDto;
    }

    @Override
    public DailyCashierReportDto getDailyCashierReport(Integer merchantId, Integer storeId, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCashierReportDto dailyCashierReportDto = new DailyCashierReportDto();
        return dailyCashierReportDto;
    }

    @Override
    public DailyCateReportDto getDailyCateReport(Integer merchantId, Integer storeId, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCateReportDto dailyCateReportDto = new DailyCateReportDto();
        return dailyCateReportDto;
    }
}
