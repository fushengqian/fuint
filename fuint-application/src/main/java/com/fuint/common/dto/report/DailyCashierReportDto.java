package com.fuint.common.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 日收银报表实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DailyCashierReportDto implements Serializable {

    @Schema(description="总订单数量")
    private Integer totalOrderCount;

    @Schema(description="总销售金额")
    private BigDecimal totalSalesAmount;

    @Schema(description="总现金支付金额")
    private BigDecimal totalCashAmount;

    @Schema(description="总微信支付金额")
    private BigDecimal totalWechatAmount;

    @Schema(description="总支付宝支付金额")
    private BigDecimal totalAliPayAmount;

    @Schema(description="总积分支付金额")
    private BigDecimal totalPointAmount;

    @Schema(description="总卡券核销金额")
    private BigDecimal totalCouponAmount;

    @Schema(description="总余额支付金额")
    private BigDecimal totalBalanceAmount;

    @Schema(description="列表数据")
    private List<DailyCashierItemDto> dataList;

    @Schema(description="总数量")
    private Integer total;
}

