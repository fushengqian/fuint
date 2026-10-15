package com.fuint.common.dto.order;

import com.fuint.common.dto.coupon.UserCouponDto;
import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员订单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserOrderDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="订单类型")
    private String type;

    @Schema(description="订单类型名称")
    private String typeName;

    @Schema(description="支付类型")
    private String payType;

    @Schema(description="订单模式")
    private String orderMode;

    @Schema(description="下单平台")
    private String platform;

    @Schema(description="是否核销")
    private Boolean isVerify;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="是否游客")
    private String isVisitor;

    @Schema(description="核销码")
    private String verifyCode;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="总金额")
    private BigDecimal amount;

    @Schema(description="支付金额")
    private BigDecimal payAmount;

    @Schema(description="优惠金额")
    private BigDecimal discount;

    @Schema(description="配送费用")
    private BigDecimal deliveryFee;

    @Schema(description="使用积分")
    private Integer usePoint;

    @Schema(description="积分金额")
    private BigDecimal pointAmount;

    @Schema(description="订单参数")
    private String param;

    @Schema(description="备注信息")
    private String remark;

    @Schema(description="创建时间")
    private String createTime;

    @Schema(description="更新时间")
    private String updateTime;

    @Schema(description="支付时间")
    private String payTime;

    @Schema(description="订单状态")
    private String status;

    @Schema(description="支付状态")
    private String payStatus;

    @Schema(description="结算状态")
    private String settleStatus;

    @Schema(description="状态说明")
    private String statusText;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="订单商品列表")
    private List<OrderGoodsDto> goods;

    @Schema(description="下单用户信息")
    private OrderUserDto userInfo;

    @Schema(description="配送地址")
    private AddressDto address;

    @Schema(description="物流信息")
    private ExpressDto expressInfo;

    @Schema(description="所属店铺信息")
    private MtStore storeInfo;

    @Schema(description="售后订单")
    private MtRefund refundInfo;

    @Schema(description="使用卡券")
    private UserCouponDto couponInfo;

    @Schema(description="使用卡券列表（储值卡叠加使用场景）")
    private List<UserCouponDto> couponInfoList;

    @Schema(description="关联员工")
    private MtStaff staffInfo;

    @Schema(description="核销状态")
    private String confirmStatus;

    @Schema(description="核销时间")
    private String confirmTime;

    @Schema(description="核销备注")
    private String confirmRemark;

}

