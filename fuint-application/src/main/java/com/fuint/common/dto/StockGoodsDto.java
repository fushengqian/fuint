package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 库存商品实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Data
public class StockGoodsDto implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编码")
    private String goodsNo;

    @ApiModelProperty("商品分类ID")
    private Integer cateId;

    @ApiModelProperty("商品数量")
    private Integer num;

    @ApiModelProperty("库存")
    private Double stock;

    @ApiModelProperty("skuId")
    private Integer skuId;

    @ApiModelProperty("商品logo")
    private String logo;

    @ApiModelProperty("状态，A：正常；D：删除")
    private String status;

}
