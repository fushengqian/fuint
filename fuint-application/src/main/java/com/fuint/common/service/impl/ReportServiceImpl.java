package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuint.common.dto.report.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.ReportService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.DateUtil;
import com.fuint.repository.mapper.MtGoodsCateMapper;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtOrderGoodsMapper;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtOrderGoods;
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
     * 订单 Mapper
     */
    private MtOrderMapper mtOrderMapper;
    
    /**
     * 订单商品 Mapper
     */
    private MtOrderGoodsMapper mtOrderGoodsMapper;
    
    /**
     * 商品分类 Mapper
     */
    private MtGoodsCateMapper mtGoodsCateMapper;
    
    /**
     * 商品 Mapper
     */
    private MtGoodsMapper mtGoodsMapper;

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
    public DailySalesReportDto getDailySalesReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailySalesReportDto dailySalesReportDto = new DailySalesReportDto();

        // 获取店铺列表
        List<MtStore> storeList = storeService.getStoreListByIds(merchantId, storeIds);

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
    public DailyCashierReportDto getDailyCashierReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCashierReportDto dailyCashierReportDto = new DailyCashierReportDto();

        // 获取店铺列表
        List<MtStore> storeList = storeService.getStoreListByIds(merchantId, storeIds);

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

        // 如果没有日期范围，默认查询最近 7 天（过去 7 天）
        if (dateList.isEmpty()) {
            for (int i = 6; i >= 0; i--) {
                 dateList.add(DateUtil.formatDate(DateUtil.getDayBegin(i), "yyyy-MM-dd"));
            }
        }

        List<DailyCashierItemDto> allDataList = new ArrayList<>();
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
                // 按收银员分组统计
                Map<Integer, List<MtOrder>> staffOrderMap = new HashMap<>();

                // 查询该店铺该日期的订单列表
                LambdaQueryWrapper<MtOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MtOrder::getStoreId, store.getId());
                wrapper.ge(MtOrder::getCreateTime, dayBegin);
                wrapper.le(MtOrder::getCreateTime, dayEnd);
                wrapper.eq(MtOrder::getPayStatus, "B");
                wrapper.notIn(MtOrder::getStatus, Arrays.asList("C", "G", "H"));

                List<MtOrder> orderList = mtOrderMapper.selectList(wrapper);

                // 按收银员分组
                for (MtOrder order : orderList) {
                    Integer staffId = order.getStaffId() != null ? order.getStaffId() : 0;
                    if (!staffOrderMap.containsKey(staffId)) {
                        staffOrderMap.put(staffId, new ArrayList<>());
                    }
                    staffOrderMap.get(staffId).add(order);
                }

                // 遍历每个收银员
                for (Map.Entry<Integer, List<MtOrder>> entry : staffOrderMap.entrySet()) {
                    Integer staffId = entry.getKey();
                    List<MtOrder> staffOrders = entry.getValue();

                    DailyCashierItemDto itemDto = new DailyCashierItemDto();
                    itemDto.setDateTime(dateStr);
                    itemDto.setStoreId(store.getId());
                    itemDto.setStoreName(store.getName());
                    itemDto.setStaffId(staffId > 0 ? staffId : null);
                    itemDto.setStaffName(staffId > 0 ? "员工" + staffId : "未知");

                    Integer orderCount = staffOrders.size();
                    BigDecimal salesAmount = new BigDecimal("0");
                    BigDecimal cashAmount = new BigDecimal("0");
                    BigDecimal wechatAmount = new BigDecimal("0");
                    BigDecimal aliPayAmount = new BigDecimal("0");
                    BigDecimal pointAmount = new BigDecimal("0");
                    BigDecimal couponAmount = new BigDecimal("0");

                    for (MtOrder order : staffOrders) {
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

        List<DailyCashierItemDto> dataList = new ArrayList<>();
        if (startIndex < total) {
            dataList = allDataList.subList(startIndex, endIndex);
        }

        dailyCashierReportDto.setTotalOrderCount(totalOrderCount);
        dailyCashierReportDto.setTotalSalesAmount(totalSalesAmount);
        dailyCashierReportDto.setTotalCashAmount(totalCashAmount);
        dailyCashierReportDto.setTotalWechatAmount(totalWechatAmount);
        dailyCashierReportDto.setTotalAliPayAmount(totalAliPayAmount);
        dailyCashierReportDto.setTotalPointAmount(totalPointAmount);
        dailyCashierReportDto.setTotalCouponAmount(totalCouponAmount);
        dailyCashierReportDto.setDataList(dataList);
        dailyCashierReportDto.setTotal(total);

        return dailyCashierReportDto;
    }

    @Override
    public DailyCateReportDto getDailyCateReport(Integer merchantId, List<Integer> storeIds, String startTime, String endTime, Integer page, Integer pageSize) throws ParseException {
        Date start = StringUtil.isNotEmpty(startTime) ? DateUtil.parseDate(startTime) : null;
        Date end = StringUtil.isNotEmpty(endTime) ? DateUtil.parseDate(endTime) : null;
        DailyCateReportDto dailyCateReportDto = new DailyCateReportDto();

        // 获取店铺列表
        List<MtStore> storeList = storeService.getStoreListByIds(merchantId, storeIds);

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

        // 如果没有日期范围，默认查询最近 7 天（过去 7 天）
        if (dateList.isEmpty()) {
            for (int i = 6; i >= 0; i--) {
                 dateList.add(DateUtil.formatDate(DateUtil.getDayBegin(i), "yyyy-MM-dd"));
            }
        }

        // 缓存商品分类信息
        Map<Integer, MtGoodsCate> cateMap = new HashMap<>();
        LambdaQueryWrapper<MtGoodsCate> cateWrapper = new LambdaQueryWrapper<>();
        cateWrapper.eq(MtGoodsCate::getMerchantId, merchantId);
        cateWrapper.eq(MtGoodsCate::getStatus, "A");
        List<MtGoodsCate> cateList = mtGoodsCateMapper.selectList(cateWrapper);
        for (MtGoodsCate cate : cateList) {
            cateMap.put(cate.getId(), cate);
        }

        // 缓存商品信息（ID -> 分类 ID 和成本价）
        Map<Integer, Integer> goodsCateMap = new HashMap<>();
        Map<Integer, BigDecimal> goodsCostMap = new HashMap<>();

        List<DailyCateItemDto> allDataList = new ArrayList<>();
        Integer totalSalesCount = 0;
        BigDecimal totalSalesAmount = new BigDecimal("0");
        Integer totalBuyerCount = 0;
        BigDecimal totalCostAmount = new BigDecimal("0");
        Set<Integer> totalBuyerSet = new HashSet<>();

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
                // 按分类分组统计
                Map<Integer, DailyCateItemDto> cateDataMap = new HashMap<>();

                // 查询该店铺该日期的订单列表
                LambdaQueryWrapper<MtOrder> orderWrapper = new LambdaQueryWrapper<>();
                orderWrapper.eq(MtOrder::getStoreId, store.getId());
                orderWrapper.ge(MtOrder::getCreateTime, dayBegin);
                orderWrapper.le(MtOrder::getCreateTime, dayEnd);
                orderWrapper.eq(MtOrder::getPayStatus, "B");
                orderWrapper.notIn(MtOrder::getStatus, Arrays.asList("C", "G", "H"));

                List<MtOrder> orderList = mtOrderMapper.selectList(orderWrapper);

                // 收集买家 ID
                for (MtOrder order : orderList) {
                    if (order.getUserId() != null) {
                        totalBuyerSet.add(order.getUserId());
                    }
                }

                // 遍历每个订单的商品
                for (MtOrder order : orderList) {
                    // 查询订单商品明细
                    LambdaQueryWrapper<MtOrderGoods> goodsWrapper = new LambdaQueryWrapper<>();
                    goodsWrapper.eq(MtOrderGoods::getOrderId, order.getId());
                    goodsWrapper.eq(MtOrderGoods::getStatus, "A");

                    List<MtOrderGoods> orderGoodsList = mtOrderGoodsMapper.selectList(goodsWrapper);

                    // 遍历订单商品，按分类统计
                    for (MtOrderGoods orderGoods : orderGoodsList) {
                        Integer goodsId = orderGoods.getGoodsId();
                        
                        // 获取商品分类 ID（从缓存中获取，如果没有则查询数据库）
                        Integer cateId = null;
                        BigDecimal costPrice = new BigDecimal("0");
                        
                        if (goodsId != null) {
                            if (!goodsCateMap.containsKey(goodsId)) {
                                // 查询商品信息
                                MtGoods goods = mtGoodsMapper.selectById(goodsId);
                                if (goods != null) {
                                    goodsCateMap.put(goodsId, goods.getCateId());
                                    goodsCostMap.put(goodsId, goods.getCostPrice() != null ? goods.getCostPrice() : new BigDecimal("0"));
                                }
                            }
                            cateId = goodsCateMap.get(goodsId);
                            costPrice = goodsCostMap.getOrDefault(goodsId, new BigDecimal("0"));
                        }
                        
                        if (!cateDataMap.containsKey(cateId)) {
                            DailyCateItemDto itemDto = new DailyCateItemDto();
                            itemDto.setDateTime(dateStr);
                            itemDto.setStoreId(store.getId());
                            itemDto.setStoreName(store.getName());
                            itemDto.setCateId(cateId);
                            itemDto.setCateName(cateId != null && cateMap.containsKey(cateId) ? 
                                cateMap.get(cateId).getName() : "其他");
                            itemDto.setSalesCount(0);
                            itemDto.setSalesAmount(new BigDecimal("0"));
                            itemDto.setBuyerCount(0);
                            itemDto.setCostAmount(new BigDecimal("0"));
                            itemDto.setProfitAmount(new BigDecimal("0"));
                            itemDto.setRefundCount(new BigDecimal("0"));
                            cateDataMap.put(cateId, itemDto);
                        }

                        DailyCateItemDto itemDto = cateDataMap.get(cateId);
                        
                        // 销售数量
                        if (orderGoods.getNum() != null) {
                            itemDto.setSalesCount(itemDto.getSalesCount() + orderGoods.getNum().intValue());
                        }
                        
                        // 销售金额（优惠价 * 数量）
                        if (orderGoods.getDiscount() != null && orderGoods.getNum() != null) {
                            BigDecimal amount = orderGoods.getDiscount().multiply(new BigDecimal(orderGoods.getNum().toString()));
                            itemDto.setSalesAmount(itemDto.getSalesAmount().add(amount));
                        }
                        
                        // 成本金额（成本价 * 数量）
                        if (orderGoods.getNum() != null) {
                            BigDecimal costAmount = costPrice.multiply(new BigDecimal(orderGoods.getNum().toString()));
                            itemDto.setCostAmount(itemDto.getCostAmount().add(costAmount));
                        }
                        
                        // 利润金额 = 销售金额 - 成本金额
                        BigDecimal profitAmount = itemDto.getSalesAmount().subtract(itemDto.getCostAmount());
                        itemDto.setProfitAmount(profitAmount);
                    }
                }

                // 将该店铺的所有分类数据添加到结果列表
                allDataList.addAll(cateDataMap.values());
            }
        }

        // 计算总计
        for (DailyCateItemDto item : allDataList) {
            totalSalesCount += item.getSalesCount();
            totalSalesAmount = totalSalesAmount.add(item.getSalesAmount());
            totalCostAmount = totalCostAmount.add(item.getCostAmount());
        }
        
        // 总购买人数（去重后的买家数）
        totalBuyerCount = totalBuyerSet.size();
        
        // 总利润 = 总销售金额 - 总成本
        BigDecimal totalProfitAmount = totalSalesAmount.subtract(totalCostAmount);

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

        List<DailyCateItemDto> dataList = new ArrayList<>();
        if (startIndex < total) {
            dataList = allDataList.subList(startIndex, endIndex);
        }

        dailyCateReportDto.setTotalSalesCount(totalSalesCount);
        dailyCateReportDto.setTotalSalesAmount(totalSalesAmount);
        dailyCateReportDto.setTotalBuyerCount(totalBuyerCount);
        dailyCateReportDto.setTotalCostAmount(totalCostAmount);
        dailyCateReportDto.setTotalProfitAmount(totalProfitAmount);
        dailyCateReportDto.setDataList(dataList);
        dailyCateReportDto.setTotal(total);

        return dailyCateReportDto;
    }
}
