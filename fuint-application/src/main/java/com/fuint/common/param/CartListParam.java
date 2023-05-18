package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 购物车列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CartListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="购物车ID", name="cartId")
    private Integer cartId;

    @ApiModelProperty(value="指定购物车ID，逗号分割", name="cartIds")
    private String cartIds;

    @ApiModelProperty(value="商品ID", name="goodsId")
    private Integer goodsId;

    @ApiModelProperty(value="卡券ID", name="couponId")
    private Integer couponId;

    @ApiModelProperty(value="商品SkuID", name="skuId")
    private Integer skuId;

    @ApiModelProperty(value="使用积分", name="point")
    private String point;

    @ApiModelProperty(value="购买数量", name="buyNum")
    private Integer buyNum;

    @ApiModelProperty(value="挂单编码", name="hangNo")
    private String hangNo;

    @ApiModelProperty(value="下单会员ID", name="userId")
    private Integer userId;

}
