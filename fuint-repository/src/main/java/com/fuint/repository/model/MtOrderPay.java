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
 * 订单支付单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_order_pay")
@Schema(name="MtOrderPay对象", description="订单支付单表")
public class MtOrderPay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="支付单号，即渠道 out_trade_no")
    private String paySn;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="支付方式")
    private String payType;

    @Schema(description="本次应付金额")
    private BigDecimal amount;

    @Schema(description="支付状态：A待支付 B已支付")
    private String payStatus;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;
}
