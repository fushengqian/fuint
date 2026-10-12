package com.fuint.common.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品排行DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsTopDto implements Serializable {

    @Schema(description="商品ID")
    private Integer id;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="商品条码")
    private String goodsNo;

    @Schema(description="销售金额")
    private BigDecimal amount;

    @Schema(description="销售数量")
    private Integer num;

}

