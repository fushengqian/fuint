package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品排行DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsTopDto implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品条码")
    private String goodsNo;

    @ApiModelProperty("销售金额")
    private BigDecimal amount;

    @ApiModelProperty("销售数量")
    private Integer num;

}

