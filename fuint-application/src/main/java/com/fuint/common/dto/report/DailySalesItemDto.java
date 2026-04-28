package com.fuint.common.dto.report;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("日期")
    private String dateTime;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单数量")
    private Integer orderCount;

    @ApiModelProperty("销售金额")
    private BigDecimal salesAmount;

    @ApiModelProperty("现金支付金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("微信支付金额")
    private BigDecimal wechatAmount;

    @ApiModelProperty("支付宝支付金额")
    private BigDecimal aliPayAmount;

    @ApiModelProperty("积分支付金额")
    private BigDecimal pointAmount;

    @ApiModelProperty("卡券核销金额")
    private BigDecimal couponAmount;

    @ApiModelProperty("余额支付金额")
    private BigDecimal balanceAmount;

}

