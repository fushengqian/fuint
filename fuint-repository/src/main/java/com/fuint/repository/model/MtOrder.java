package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_order")
@Schema(name="MtOrder对象", description="订单表")
public class MtOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="订单类型")
    private String type;

    @Schema(description="支付类型")
    private String payType;

    @Schema(description="订单模式")
    private String orderMode;

    @Schema(description="下单平台")
    private String platform;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="支付单号，每次发起支付生成，用于渠道下单与回调反查")
    private String paySn;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="核销验证码")
    private String verifyCode;

    @Schema(description="是否游客")
    private String isVisitor;

    @Schema(description="订单金额")
    private BigDecimal amount;

    @Schema(description="支付金额")
    private BigDecimal payAmount;

    @Schema(description="结算状态")
    private String settleStatus;

    @Schema(description="使用积分数量")
    private Integer usePoint;

    @Schema(description="积分金额")
    private BigDecimal pointAmount;

    @Schema(description="折扣金额")
    private BigDecimal discount;

    @Schema(description="配送费用")
    private BigDecimal deliveryFee;

    @Schema(description="订单参数")
    private String param;

    @Schema(description="物流信息")
    private String expressInfo;

    @Schema(description="用户备注")
    private String remark;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="订单状态")
    private String status;

    @Schema(description="支付时间")
    private Date payTime;

    @Schema(description="支付状态")
    private String payStatus;

    @Schema(description="操作员工")
    private Integer staffId;

    @Schema(description="核销状态")
    private String confirmStatus;

    @Schema(description="核销时间")
    private Date confirmTime;

    @Schema(description="核销备注")
    private String confirmRemark;

    @Schema(description="分佣提成用户ID")
    private Integer commissionUserId;

    @Schema(description="分佣提成计算状态")
    private String commissionStatus;

    @Schema(description="最后操作人")
    private String operator;

}
