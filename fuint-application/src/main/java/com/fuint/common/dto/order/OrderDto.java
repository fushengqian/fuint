package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="订单类型")
    private String type;

    @Schema(description="下单平台")
    private String platform;

    @Schema(description="支付类型")
    private String payType;

    @Schema(description="订单类型名称")
    private String orderMode;

    @Schema(description="核销码")
    private String verifyCode;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="卡券ID列表，逗号分隔（储值卡叠加使用）")
    private String couponIds;

    @Schema(description="购物车ID")
    private String cartIds;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="skuID")
    private Integer skuId;

    @Schema(description="购买数量")
    private Double buyNum;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="是否游客")
    private String isVisitor;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="订单金额")
    private BigDecimal amount;

    @Schema(description="支付金额")
    private BigDecimal payAmount;

    @Schema(description="使用积分数量")
    private Integer usePoint;

    @Schema(description="积分金额")
    private BigDecimal pointAmount;

    @Schema(description="折扣金额")
    private BigDecimal discount;

    @Schema(description="配送费用")
    private BigDecimal deliveryFee;

    @Schema(description="物流信息")
    private ExpressDto expressInfo;

    @Schema(description="订单参数")
    private String param;

    @Schema(description="用户备注")
    private String remark;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="支付时间")
    private Date payTime;

    @Schema(description="订单状态")
    private String status;

    @Schema(description="支付状态")
    private String payStatus;

    @Schema(description="结算状态")
    private String settleStatus;

    @Schema(description="核销状态")
    private String confirmStatus;

    @Schema(description="核销时间")
    private Date confirmTime;

    @Schema(description="核销备注")
    private String confirmRemark;

    @Schema(description="最后操作人")
    private String operator;

}

