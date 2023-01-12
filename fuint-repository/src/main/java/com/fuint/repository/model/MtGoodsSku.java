package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品SKU表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_goods_sku")
@ApiModel(value = "MtGoodsSku对象", description = "商品SKU表")
public class MtGoodsSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("sku编码")
    private String skuNo;

    @ApiModelProperty("图片")
    private String logo;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("规格ID")
    private String specIds;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("划线价格")
    private BigDecimal linePrice;

    @ApiModelProperty("重量")
    private BigDecimal weight;

    @ApiModelProperty("状态")
    private String status;


}
