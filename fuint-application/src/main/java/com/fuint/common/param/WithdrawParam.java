package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 佣金提现请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class WithdrawParam implements Serializable {

    @ApiModelProperty(value="用户ID")
    private Integer userId;

    @ApiModelProperty("提现金额")
    private BigDecimal amount;

    @ApiModelProperty(value="备注说明")
    private String remark;

}
