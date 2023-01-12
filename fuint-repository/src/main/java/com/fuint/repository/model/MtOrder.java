package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_order")
@ApiModel(value = "MtOrder对象", description = "订单表")
public class MtOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("订单类型")
    private String type;

    @ApiModelProperty("支付类型")
    private String payType;

    @ApiModelProperty("订单模式")
    private String orderMode;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("核销验证码")
    private String verifyCode;

    @ApiModelProperty("是否游客")
    private String isVisitor;

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

    @ApiModelProperty("订单参数")
    private String param;

    @ApiModelProperty("物流信息")
    private String expressInfo;

    @ApiModelProperty("用户备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("支付时间")
    private Date payTime;

    @ApiModelProperty("支付状态")
    private String payStatus;

    @ApiModelProperty("操作员工")
    private Integer staffId;

    @ApiModelProperty("最后操作人")
    private String operator;


}
