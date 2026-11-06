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

    @Schema(description="奖励场景：register 注册奖励；order 交易奖励")
    private String scene;

    @Schema(description="奖励类型：coupon 卡券；point 积分")
    private String rewardType;

    @Schema(description="奖励卡券ID")
    private Integer couponId;

    @Schema(description="奖励卡券名称")
    private String couponName;

    @Schema(description="奖励卡券张数")
    private Integer couponNum;

    @Schema(description="奖励积分")
    private Integer point;

    @Schema(description="散客值")
    private BigDecimal visitorVal;

    @Schema(description="会员值")
    private BigDecimal memberVal;

    @Schema(description="二级散客值")
    private BigDecimal subVisitorVal;

    @Schema(description="二级会员值")
    private BigDecimal subMemberVal;

}
