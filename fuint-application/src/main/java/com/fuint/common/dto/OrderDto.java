package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class OrderDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("订单类型")
    private String type;

    @ApiModelProperty("下单平台")
    private String platform;

    @ApiModelProperty("支付类型")
    private String payType;

    @ApiModelProperty("订单类型名称")
    private String orderMode;

    @ApiModelProperty("核销码")
    private String verifyCode;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("购物车ID")
    private String cartIds;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("skuID")
    private Integer skuId;

    @ApiModelProperty("购买数量")
    private Integer buyNum;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("是否游客")
    private String isVisitor;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("员工ID")
    private Integer staffId;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("使用积分数量")
    private Integer usePoint;

    @ApiModelProperty("积分金额")
    private BigDecimal pointAmount;

    @ApiModelProperty("折扣金额")
    private BigDecimal discount;

    @ApiModelProperty("配送费用")
    private BigDecimal deliveryFee;

    @ApiModelProperty("物流信息")
    private ExpressDto expressInfo;

    @ApiModelProperty("订单参数")
    private String param;

    @ApiModelProperty("用户备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("支付时间")
    private Date payTime;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("支付状态")
    private String payStatus;

    @ApiModelProperty(value="结算状态")
    private String settleStatus;

    @ApiModelProperty("核销状态")
    private String confirmStatus;

    @ApiModelProperty("核销时间")
    private Date confirmTime;

    @ApiModelProperty("核销备注")
    private String confirmRemark;

    @ApiModelProperty("最后操作人")
    private String operator;

}

