package com.fuint.repository.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@Schema(name="商品对象", description="商品对象")
public class GoodsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="商品图片")
    private String logo;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="商品编码")
    private String goodsNo;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="商品价格")
    private BigDecimal price;

    @Schema(description="初始销量")
    private Double initSale;

    @Schema(description="商品库存")
    private Double stock;

    @Schema(description="商品规格")
    private String specIds;

    @Schema(description="sku价格")
    private String skuPrice;

    @Schema(description="sk库存")
    private Integer skuStock;

}
