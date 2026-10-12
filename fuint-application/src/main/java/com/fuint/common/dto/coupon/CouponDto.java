package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="卡券名称")
    private String name;

    @Schema(description="卡券类型")
    private String type;

    @Schema(description="内容")
    private Integer content;

    @Schema(description="状态")
    private String status;

    @Schema(description="获取规则")
    private String inRule;

    @Schema(description="使用规则")
    private String outRule;

    @Schema(description="图片")
    private String image;

    @Schema(description="面额")
    private BigDecimal amount;

    @Schema(description="领取需要积分数量")
    private Integer point;

    @Schema(description="卖点")
    private String sellingPoint;

    @Schema(description="已领取、预存数量")
    private Integer gotNum;

    @Schema(description="剩余数量")
    private Integer leftNum;

    @Schema(description="发行数量")
    private Integer total;

    @Schema(description="限制数量")
    private Integer limitNum;

    @Schema(description="适用店铺")
    private String storeNames;

    @Schema(description="是否领取")
    private Boolean isReceive;

    @Schema(description="是否需要领取码")
    private boolean needReceiveCode;

    @Schema(description="会员卡券ID")
    private int userCouponId;

    @Schema(description="有效期")
    private String effectiveDate;

    @Schema(description="卡券说明")
    private String description;

}
