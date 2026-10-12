package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品SKU表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_goods_sku")
@Schema(name="MtGoodsSku对象", description="商品SKU表")
public class MtGoodsSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="sku编码")
    private String skuNo;

    @Schema(description="图片")
    private String logo;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="规格ID")
    private String specIds;

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

    @Schema(description="状态")
    private String status;

}
