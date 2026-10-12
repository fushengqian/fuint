package com.fuint.common.dto.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销提成概览数据实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionOverviewDto implements Serializable {

    @Schema(description="总佣金")
    private BigDecimal totalAmount;

    @Schema(description="待提现金额")
    private BigDecimal amount;

    @Schema(description="已提现金额")
    private BigDecimal withdrawAmount;

    @Schema(description="邀请会员数")
    private BigDecimal userCount;

    @Schema(description="订单数")
    private BigDecimal orderCount;

}
