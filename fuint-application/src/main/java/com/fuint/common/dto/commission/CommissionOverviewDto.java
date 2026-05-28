package com.fuint.common.dto.commission;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("总佣金")
    private BigDecimal totalAmount;

    @ApiModelProperty("待提现金额")
    private BigDecimal amount;

    @ApiModelProperty("已提现金额")
    private BigDecimal withdrawAmount;

    @ApiModelProperty("邀请会员数")
    private BigDecimal userCount;

    @ApiModelProperty("订单数")
    private BigDecimal orderCount;

}
