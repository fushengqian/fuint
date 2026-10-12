package com.fuint.common.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="日期")
    private String dateTime;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="店铺名称")
    private String storeName;

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="分类名称")
    private String cateName;

    @Schema(description="销售数量")
    private Integer salesCount;

    @Schema(description="销售金额")
    private BigDecimal salesAmount;

    @Schema(description="购买人数")
    private Integer buyerCount;

    @Schema(description="销售成本")
    private BigDecimal costAmount;

    @Schema(description="利润金额")
    private BigDecimal profitAmount;

    @Schema(description="退款数量")
    private BigDecimal refundCount;

}

