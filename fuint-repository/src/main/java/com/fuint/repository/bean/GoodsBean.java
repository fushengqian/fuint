package com.fuint.repository.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@ApiModel(value = "商品对象", description = "商品对象")
public class GoodsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品图片")
    private String logo;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品编码")
    private String goodsNo;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("商品库存")
    private Integer stock;

    @ApiModelProperty("商品规格")
    private String specIds;

    @ApiModelProperty("sku价格")
    private String skuPrice;

    @ApiModelProperty("sk库存")
    private Integer skuStock;

}
