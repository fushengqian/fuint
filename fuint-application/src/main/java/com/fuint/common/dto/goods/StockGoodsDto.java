package com.fuint.common.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="商品ID")
    private Integer id;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="商品编码")
    private String goodsNo;

    @Schema(description="商品分类ID")
    private Integer cateId;

    @Schema(description="商品数量")
    private Double num;

    @Schema(description="库存")
    private Double stock;

    @Schema(description="skuId")
    private Integer skuId;

    @Schema(description="商品logo")
    private String logo;

    @Schema(description="状态，A：正常；D：删除")
    private String status;

}
