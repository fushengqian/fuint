package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销提成规则项目实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleItemDto implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品logo")
    private String logo;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("方案类型,goods:商品销售；coupon：卡券销售；recharge：会员充值")
    private String type;

    @ApiModelProperty("提成方式")
    private String method;

    @ApiModelProperty("散客值")
    private BigDecimal visitorVal;

    @ApiModelProperty("会员值")
    private BigDecimal memberVal;

}
