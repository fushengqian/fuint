package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 购物车列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CartListParam extends PageParam implements Serializable {

    @Schema(description="购物车ID", name="cartId")
    private Integer cartId;

    @Schema(description="指定购物车ID，逗号分割", name="cartIds")
    private String cartIds;

    @Schema(description="商品ID", name="goodsId")
    private Integer goodsId;

    @Schema(description="卡券ID", name="couponId")
    private Integer couponId;

    @Schema(description="卡券ID列表，逗号分隔（储值卡叠加使用）", name="couponIds")
    private String couponIds;

    @Schema(description="商品SkuID", name="skuId")
    private Integer skuId;

    @Schema(description="使用积分", name="point")
    private String point;

    @Schema(description="购买数量", name="buyNum")
    private Double buyNum;

    @Schema(description="挂单编码", name="hangNo")
    private String hangNo;

    @Schema(description="下单会员ID", name="userId")
    private Integer userId;

    @Schema(description="订单模式", name="orderMode")
    private String orderMode;

}
