package com.fuint.common.dto;

import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 会员订单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class UserOrderDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单类型")
    private String type;

    @ApiModelProperty("订单类型名称")
    private String typeName;

    @ApiModelProperty("支付类型")
    private String payType;

    @ApiModelProperty("订单模式")
    private String orderMode;

    @ApiModelProperty("下单平台")
    private String platform;

    @ApiModelProperty("是否核销")
    private Boolean isVerify;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("是否游客")
    private String isVisitor;

    @ApiModelProperty("核销码")
    private String verifyCode;

    @ApiModelProperty("员工ID")
    private Integer staffId;

    @ApiModelProperty("总金额")
    private BigDecimal amount;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("优惠金额")
    private BigDecimal discount;

    @ApiModelProperty("配送费用")
    private BigDecimal deliveryFee;

    @ApiModelProperty("使用积分")
    private Integer usePoint;

    @ApiModelProperty("积分金额")
    private BigDecimal pointAmount;

    @ApiModelProperty("订单参数")
    private String param;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("支付时间")
    private String payTime;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("支付状态")
    private String payStatus;

    @ApiModelProperty(value="结算状态")
    private String settleStatus;

    @ApiModelProperty("状态说明")
    private String statusText;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("订单商品列表")
    private List<OrderGoodsDto> goods;

    @ApiModelProperty("下单用户信息")
    private OrderUserDto userInfo;

    @ApiModelProperty("配送地址")
    private AddressDto address;

    @ApiModelProperty("物流信息")
    private ExpressDto expressInfo;

    @ApiModelProperty("所属店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("售后订单")
    private MtRefund refundInfo;

    @ApiModelProperty("使用卡券")
    private UserCouponDto couponInfo;

    @ApiModelProperty("所属员工")
    private MtStaff staffInfo;

    @ApiModelProperty("核销状态")
    private String confirmStatus;

    @ApiModelProperty("核销时间")
    private String confirmTime;

    @ApiModelProperty("核销备注")
    private String confirmRemark;

}

