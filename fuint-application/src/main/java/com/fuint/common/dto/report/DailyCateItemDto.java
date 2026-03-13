package com.fuint.common.dto.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 报表日分类实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DailyCateItemDto implements Serializable {

    @ApiModelProperty("日期")
    private String dateTime;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("销售数量")
    private Integer salesCount;

    @ApiModelProperty("销售金额")
    private BigDecimal salesAmount;

    @ApiModelProperty("购买人数")
    private Integer buyerCount;

    @ApiModelProperty("销售成本")
    private BigDecimal costAmount;

    @ApiModelProperty("利润金额")
    private BigDecimal profitAmount;

    @ApiModelProperty("退款数量")
    private BigDecimal refundCount;

}

