package com.fuint.common.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="商品ID")
    private Integer id;

    @Schema(description="商品ID（别名，兼容）")
    private Integer goodsId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="商品编码")
    private String goodsNo;

    @Schema(description="商品分类ID")
    private Integer cateId;

    @Schema(description="系统库存")
    private Double systemStock;

    @Schema(description="实际库存")
    private Double actualStock;

    @Schema(description="差异数量")
    private Double diffStock;

    @Schema(description="skuId")
    private Integer skuId;

    @Schema(description="商品logo")
    private String logo;

    @Schema(description="状态，A：正常；D：删除")
    private String status;

    @Schema(description="备注")
    private String description;

    @Schema(description="是否单规格 Y/N")
    private String isSingleSpec;
}
