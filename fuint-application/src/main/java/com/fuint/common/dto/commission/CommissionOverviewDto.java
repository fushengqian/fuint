package com.fuint.common.dto.commission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销提成概率数据实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionOverviewDto implements Serializable {

    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;

}
