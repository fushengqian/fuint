package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单收货地址记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_order_address")
@ApiModel(value = "MtOrderAddress对象", description = "订单收货地址记录表")
public class MtOrderAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("地址ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("收货人姓名")
    private String name;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("省份ID")
    private Integer provinceId;

    @ApiModelProperty("城市ID")
    private Integer cityId;

    @ApiModelProperty("区/县ID")
    private Integer regionId;

    @ApiModelProperty("详细地址")
    private String detail;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("创建时间")
    private Date createTime;


}
