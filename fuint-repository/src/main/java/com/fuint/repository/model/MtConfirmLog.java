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
 * 核销记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_confirm_log")
@ApiModel(value = "MtConfirmLog对象", description = "核销记录表")
public class MtConfirmLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("核销金额")
    private BigDecimal amount;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("用户券ID")
    private Integer userCouponId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("卡券所属用户ID")
    private Integer userId;

    @ApiModelProperty("核销者用户ID")
    private Integer operatorUserId;

    @ApiModelProperty("核销店铺ID")
    private Integer storeId;

    @ApiModelProperty("状态，A正常核销；D：撤销使用")
    private String status;

    @ApiModelProperty("撤销时间")
    private Date cancelTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("操作来源user_id对应表t_account 还是 mt_user")
    private String operatorFrom;

    @ApiModelProperty("备注信息")
    private String remark;


}
