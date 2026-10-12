package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="商品名称")
    private String goodsName;

    @Schema(description="提成方式")
    private String method;

    @Schema(description="散客值")
    private BigDecimal visitorVal;

    @Schema(description="会员值")
    private BigDecimal memberVal;

    @Schema(description="二级散客值")
    private BigDecimal subVisitorVal;

    @Schema(description="二级会员值")
    private BigDecimal subMemberVal;

}
