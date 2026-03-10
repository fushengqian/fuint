package com.fuint.common.service;

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

}
