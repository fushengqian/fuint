package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销提成规则项目请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleItemParam implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("提成方式")
    private String method;

    @ApiModelProperty("散客值")
    private BigDecimal visitorVal;

    @ApiModelProperty("会员值")
    private BigDecimal memberVal;

}
