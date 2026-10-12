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
public class DailyCateReportDto implements Serializable {

    @Schema(description="总销售数量")
    private Integer totalSalesCount;

    @Schema(description="总销售金额")
    private BigDecimal totalSalesAmount;

    @Schema(description="总购买人数")
    private Integer totalBuyerCount;

    @Schema(description="总销售成本")
    private BigDecimal totalCostAmount;

    @Schema(description="总利润金额")
    private BigDecimal totalProfitAmount;

    @Schema(description="总退款数量")
    private BigDecimal totalRefundCount;

    @Schema(description="列表数据")
    private List<DailyCateItemDto> dataList;

    @Schema(description="总数量")
    private Integer total;
}

