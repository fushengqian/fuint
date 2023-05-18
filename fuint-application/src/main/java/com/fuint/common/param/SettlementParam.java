package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单结算请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementParam implements Serializable {

    @ApiModelProperty(value="购物车Id，逗号分隔", name="cartIds")
    private String cartIds;

    @ApiModelProperty(value="购买对象，购买储值卡、升级会员等级必填", name="targetId")
    private String targetId;

    @ApiModelProperty(value="购买储值卡数量", name="selectNum")
    private String selectNum;

    @ApiModelProperty(value="结算备注", name="remark")
    private String remark;

    @ApiModelProperty(value="订单类型，payment：付款订单、goods：商品订单、recharge：充值订单、prestore：储值卡订单、member：会员升级订单", name="type")
    private String type;

    @ApiModelProperty(value="支付金额，付款类订单必填", name="payAmount")
    private String payAmount;

    @ApiModelProperty(value="使用积分数量", name="usePoint")
    private Integer usePoint;

    @ApiModelProperty(value="使用卡券ID", name="couponId")
    private Integer couponId;

    @ApiModelProperty(value="支付类型，CASH：现金支付，JSAPI：微信支付，MICROPAY：微信扫码支付，BALANCE：余额支付，ALISCAN：支付宝扫码", name="payType")
    private String payType;

    @ApiModelProperty(value="PC端扫码支付的二维码", name="authCode")
    private String authCode;

    @ApiModelProperty(value="会员ID（代客下单用到）", name="userId")
    private Integer userId;

    @ApiModelProperty(value="会员手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="实付金额（收银台用到）", name="cashierPayAmount")
    private String cashierPayAmount;

    @ApiModelProperty(value="优惠金额（收银台用到）", name="cashierDiscountAmount")
    private String cashierDiscountAmount;

    @ApiModelProperty(value="商品ID", name="goodsId")
    private Integer goodsId;

    @ApiModelProperty(value="商品skuID", name="skuId")
    private Integer skuId;

    @ApiModelProperty(value="购买数量", name="buyNum")
    private Integer buyNum;

    @ApiModelProperty(value="订单模式，配送（express）或自提（oneself）", name="orderMode")
    private String orderMode;

    @ApiModelProperty(value="订单ID", name="orderId")
    private Integer orderId;

}
