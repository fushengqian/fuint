package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ReqCouponDto implements Serializable {

    @Schema(description="卡券ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="分组ID")
    private Integer groupId;

    @Schema(description="类型")
    private String type;

    @Schema(description="内容")
    private Integer content;

    @Schema(description="名称")
    private String name;

    @Schema(description="是否允许转赠")
    private Integer isGive;

    @Schema(description="获得卡券所消耗积分")
    private Integer point;

    @Schema(description="获得计次卡卡所消耗积分")
    private Integer timerPoint;

    @Schema(description="领取码")
    private String receiveCode;

    @Schema(description="使用专项")
    private String useFor;

    @Schema(description="过期类型")
    private String expireType;

    @Schema(description="有效天数")
    private Integer expireTime;

    @Schema(description="计次卡领取码")
    private String timerReceiveCode;

    @Schema(description="有效期开始时间")
    private String beginTime;

    @Schema(description="有效期结束时间")
    private String endTime;

    @Schema(description="价值金额")
    private BigDecimal amount;

    @Schema(description="发放方式")
    private String sendWay;

    @Schema(description="适用商品")
    private String applyGoods;

    @Schema(description="每次发放数量")
    private Integer sendNum;

    @Schema(description="发行总数量")
    private Integer total;

    @Schema(description="每人最多拥有数量")
    private Integer limitNum;

    @Schema(description="例外时间")
    private String exceptTime;

    @Schema(description="适用店铺ID，逗号分隔")
    private String storeIds;

    @Schema(description="会员等级ID，逗号分隔")
    private String gradeIds;

    @Schema(description="适用商品")
    private String goodsIds;

    @Schema(description="后台备注")
    private String remarks;

    @Schema(description="图片")
    private String image;

    @Schema(description="备注")
    private String description;

    @Schema(description="预存规则")
    private String inRule;

    @Schema(description="核销规则")
    private String outRule;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
