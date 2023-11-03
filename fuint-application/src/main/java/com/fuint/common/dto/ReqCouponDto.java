package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ReqCouponDto implements Serializable {

    @ApiModelProperty("卡券ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("分组ID")
    private Integer groupId;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("是否允许转赠")
    private Integer isGive;

    @ApiModelProperty("获得卡券所消耗积分")
    private Integer point;

    @ApiModelProperty("获得计次卡卡所消耗积分")
    private Integer timerPoint;

    @ApiModelProperty("领取码")
    private String receiveCode;

    @ApiModelProperty("使用专项")
    private String useFor;

    @ApiModelProperty("过期类型")
    private String expireType;

    @ApiModelProperty("有效天数")
    private Integer expireTime;

    @ApiModelProperty("计次卡领取码")
    private String timerReceiveCode;

    @ApiModelProperty("有效期开始时间")
    private String beginTime;

    @ApiModelProperty("有效期结束时间")
    private String endTime;

    @ApiModelProperty("价值金额")
    private BigDecimal amount;

    @ApiModelProperty("发放方式")
    private String sendWay;

    @ApiModelProperty("适用商品")
    private String applyGoods;

    @ApiModelProperty("每次发放数量")
    private Integer sendNum;

    @ApiModelProperty("发行总数量")
    private Integer total;

    @ApiModelProperty("每人最多拥有数量")
    private Integer limitNum;

    @ApiModelProperty("例外时间")
    private String exceptTime;

    @ApiModelProperty("适用店铺ID，逗号分隔")
    private String storeIds;

    @ApiModelProperty("会员等级ID，逗号分隔")
    private String gradeIds;

    @ApiModelProperty("适用商品")
    private String goodsIds;

    @ApiModelProperty("后台备注")
    private String remarks;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("预存规则")
    private String inRule;

    @ApiModelProperty("核销规则")
    private String outRule;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
