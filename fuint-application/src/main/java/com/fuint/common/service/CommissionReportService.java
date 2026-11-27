package com.fuint.common.service;

import java.util.List;
import java.util.Map;

/**
 * 分佣提成报表统计服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionReportService {

    /**
     * 分佣提成概览统计
     * @param merchantId 商户ID（0表示全部）
     * @return 总分佣记录数、待审核分佣记录数、总提现金额、已提现金额、待提现金额、待审核佣金金额、获得佣金人数
     */
    Map<String, Object> getOverview(Integer merchantId);

    /**
     * 近一个月每日佣金变化趋势
     * @param merchantId 商户ID（0表示全部）
     * @return [{date, amount}]
     */
    List<Map<String, Object>> getTrend(Integer merchantId);

    /**
     * 会员分佣月榜单（按佣金金额取前30名）
     * @param merchantId 商户ID（0表示全部）
     * @param month 月份，格式 yyyy-MM，为空统计全部
     */
    List<Map<String, Object>> getMemberRank(Integer merchantId, String month);

    /**
     * 员工提成月榜单（全部员工）
     * @param merchantId 商户ID（0表示全部）
     * @param month 月份，格式 yyyy-MM，为空统计全部
     */
    List<Map<String, Object>> getStaffRank(Integer merchantId, String month);
}
