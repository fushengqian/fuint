package com.fuint.common.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 日销售实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DailySalesItemDto implements Serializable {

    @Schema(description="日期")
    private String dateTime;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="店铺名称")
    private String storeName;

    @Schema(description="订单数量")
    private Integer orderCount;

    @Schema(description="销售金额")
    private BigDecimal salesAmount;

    @Schema(description="现金支付金额")
    private BigDecimal cashAmount;

    @Schema(description="微信支付金额")
    private BigDecimal wechatAmount;

    @Schema(description="支付宝支付金额")
    private BigDecimal aliPayAmount;

    @Schema(description="积分支付金额")
    private BigDecimal pointAmount;

    @Schema(description="卡券核销金额")
    private BigDecimal couponAmount;

    @Schema(description="余额支付金额")
    private BigDecimal balanceAmount;

}

