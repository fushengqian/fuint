package com.fuint.common.dto.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 盘点商品DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StockCheckGoodsDto implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品ID（别名，兼容）")
    private Integer goodsId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编码")
    private String goodsNo;

    @ApiModelProperty("商品分类ID")
    private Integer cateId;

    @ApiModelProperty("系统库存")
    private Double systemStock;

    @ApiModelProperty("实际库存")
    private Double actualStock;

    @ApiModelProperty("差异数量")
    private Double diffStock;

    @ApiModelProperty("skuId")
    private Integer skuId;

    @ApiModelProperty("商品logo")
    private String logo;

    @ApiModelProperty("状态，A：正常；D：删除")
    private String status;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("是否单规格 Y/N")
    private String isSingleSpec;
}
