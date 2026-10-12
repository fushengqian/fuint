package com.fuint.repository.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品排行对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@Schema(name="商品排行对象", description="商品排行对象")
public class GoodsTopBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @Schema(description="商品ID")
    private Integer id;

    /**
     * 商品名称
     */
    @Schema(description="商品名称")
    private String name;

    /**
     * 商品条码
     */
    @Schema(description="商品条码")
    private String goodsNo;

    /**
     * 销售金额
     */
    @Schema(description="销售金额")
    private BigDecimal amount;

    /**
     * 销售数量
     */
    @Schema(description="销售数量")
    private Integer num;

}
