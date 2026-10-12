package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="提现金额")
    private BigDecimal amount;

    @Schema(description="备注说明")
    private String remark;

}
