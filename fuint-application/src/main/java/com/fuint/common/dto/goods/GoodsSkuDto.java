package com.fuint.common.dto.goods;

import com.fuint.repository.model.MtGoodsSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.math.BigDecimal;

/**
 * 商品sku实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsSkuDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="sku编码")
    private String skuNo;

    @Schema(description="图片")
    private String logo;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="规格ID")
    private String specIds;

    @Schema(description="规格列表")
    private List<MtGoodsSpec> specList;

    @Schema(description="库存")
    private Double stock;

    @Schema(description="价格")
    private BigDecimal price;

    @Schema(description="划线价格")
    private BigDecimal linePrice;

    @Schema(description="成本价格")
    private BigDecimal costPrice;

    @Schema(description="重量")
    private BigDecimal weight;

    @Schema(description="附赠卡券ID，多个逗号隔开")
    private String couponIds;

}
