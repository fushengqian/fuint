package com.fuint.repository.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品排行对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@ApiModel(value = "商品排行对象", description = "商品排行对象")
public class GoodsTopBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @ApiModelProperty("商品ID")
    private Integer id;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String name;

    /**
     * 商品条码
     */
    @ApiModelProperty("商品条码")
    private String goodsNo;

    /**
     * 销售金额
     */
    @ApiModelProperty("销售金额")
    private BigDecimal amount;

    /**
     * 销售数量
     */
    @ApiModelProperty("销售数量")
    private Integer num;

}
