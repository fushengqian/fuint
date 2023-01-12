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
 * 售后表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_refund")
@ApiModel(value = "MtRefund对象", description = "售后表")
public class MtRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("退款金额")
    private BigDecimal amount;

    @ApiModelProperty("售后类型")
    private String type;

    @ApiModelProperty("退款备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("图片")
    private String images;

    @ApiModelProperty("最后操作人")
    private String operator;


}
