package com.fuint.common.dto.report;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("总订单数量")
    private Integer totalOrderCount;

    @ApiModelProperty("总销售金额")
    private BigDecimal totalSalesAmount;

    @ApiModelProperty("总现金支付金额")
    private BigDecimal totalCashAmount;

    @ApiModelProperty("总微信支付金额")
    private BigDecimal totalWechatAmount;

    @ApiModelProperty("总支付宝支付金额")
    private BigDecimal totalAliPayAmount;

    @ApiModelProperty("总积分支付金额")
    private BigDecimal totalPointAmount;

    @ApiModelProperty("总卡券核销金额")
    private BigDecimal totalCouponAmount;

    @ApiModelProperty("总余额支付金额")
    private BigDecimal totalBalanceAmount;

    @ApiModelProperty("列表数据")
    private List<DailyCashierItemDto> dataList;

    @ApiModelProperty("总数量")
    private Integer total;
}

