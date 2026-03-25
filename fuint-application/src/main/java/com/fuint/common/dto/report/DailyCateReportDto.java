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
public class DailyCateReportDto implements Serializable {

    @ApiModelProperty("总销售数量")
    private Integer totalSalesCount;

    @ApiModelProperty("总销售金额")
    private BigDecimal totalSalesAmount;

    @ApiModelProperty("总购买人数")
    private Integer totalBuyerCount;

    @ApiModelProperty("总销售成本")
    private BigDecimal totalCostAmount;

    @ApiModelProperty("总利润金额")
    private BigDecimal totalProfitAmount;

    @ApiModelProperty("总退款数量")
    private BigDecimal totalRefundCount;

    @ApiModelProperty("列表数据")
    private List<DailyCateItemDto> dataList;

    @ApiModelProperty("总数量")
    private Integer total;
}

