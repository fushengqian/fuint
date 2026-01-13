package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("充值金额")
    private String rechargeAmount;

    @ApiModelProperty("赠送金额")
    private String giveAmount;

    @ApiModelProperty("赠送卡券ID")
    private String giveCouponIds;

}
