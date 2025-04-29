package com.fuint.common.dto;

import com.fuint.repository.model.MtGoodsSpec;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.math.BigDecimal;

/**
 * 商品sku实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSkuDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("sku编码")
    private String skuNo;

    @ApiModelProperty("图片")
    private String logo;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("规格ID")
    private String specIds;

    @ApiModelProperty("规格列表")
    private List<MtGoodsSpec> specList;

    @ApiModelProperty("库存")
    private Double stock;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("划线价格")
    private BigDecimal linePrice;

    @ApiModelProperty("重量")
    private BigDecimal weight;

}
