package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("卡券名称")
    private String name;

    @ApiModelProperty("卡券类型")
    private String type;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("获取规则")
    private String inRule;

    @ApiModelProperty("使用规则")
    private String outRule;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("面额")
    private BigDecimal amount;

    @ApiModelProperty("领取需要积分数量")
    private Integer point;

    @ApiModelProperty("卖点")
    private String sellingPoint;

    @ApiModelProperty("已领取、预存数量")
    private Integer gotNum;

    @ApiModelProperty("剩余数量")
    private Integer leftNum;

    @ApiModelProperty("限制数量")
    private Integer limitNum;

    @ApiModelProperty("是否领取")
    private Boolean isReceive;

    @ApiModelProperty("是否需要领取码")
    private boolean needReceiveCode;

    @ApiModelProperty("会员卡券ID")
    private int userCouponId;

    @ApiModelProperty("有效期")
    private String effectiveDate;

    @ApiModelProperty("卡券说明")
    private String description;

}
