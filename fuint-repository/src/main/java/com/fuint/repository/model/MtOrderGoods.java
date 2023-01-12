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
 * <p>
 * 订单商品表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_order_goods")
@ApiModel(value = "MtOrderGoods对象", description = "订单商品表")
public class MtOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("skuID")
    private Integer skuId;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("优惠价")
    private BigDecimal discount;

    @ApiModelProperty("商品数量")
    private Integer num;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("A：正常；D：删除")
    private String status;


}
