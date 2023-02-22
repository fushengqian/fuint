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
 * 购物车
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_cart")
@ApiModel(value = "MtCart对象", description = "购物车")
public class MtCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("是否游客")
    private String isVisitor;

    @ApiModelProperty("挂单号")
    private String hangNo;

    @ApiModelProperty("skuID")
    private Integer skuId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("数量")
    private Integer num;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private String status;
}
