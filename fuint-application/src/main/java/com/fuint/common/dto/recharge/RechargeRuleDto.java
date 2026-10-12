package com.fuint.common.dto.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 充值规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RechargeRuleDto implements Serializable {

    @Schema(description="充值金额")
    private String rechargeAmount;

    @Schema(description="赠送金额")
    private String giveAmount;

    @Schema(description="赠送卡券ID")
    private String giveCouponIds;

}
