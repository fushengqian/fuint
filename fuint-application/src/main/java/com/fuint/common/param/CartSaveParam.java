package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 保存购物车请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CartSaveParam implements Serializable {

    @Schema(description="购物车ID", name="cartId")
    private Integer cartId;

    @Schema(description="商品ID", name="goodsId")
    private Integer goodsId;

    @Schema(description="商品SkuID", name="skuId")
    private Integer skuId;

    @Schema(description="商品编码", name="skuNo")
    private String skuNo;

    @Schema(description="购买数量", name="buyNum")
    private Double buyNum;

    @Schema(description="操作类型，+：增加，-：减少", name="action")
    private String action;

    @Schema(description="挂单编码", name="hangNo")
    private String hangNo;

    @Schema(description="下单会员ID", name="userId")
    private Integer userId;

}
