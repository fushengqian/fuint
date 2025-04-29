package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 订单商品实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class OrderGoodsDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("订单类型")
    private String type;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("价格")
    private String price;

    @ApiModelProperty("折扣")
    private String discount;

    @ApiModelProperty("购买数量")
    private Double num;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("skuId")
    private Integer skuId;

    @ApiModelProperty("规格列表")
    private List<GoodsSpecValueDto> specList;

}

