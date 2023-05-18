package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 保存购物车请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CartSaveParam implements Serializable {

    @ApiModelProperty(value="购物车ID", name="cartId")
    private Integer cartId;

    @ApiModelProperty(value="商品ID", name="goodsId")
    private Integer goodsId;

    @ApiModelProperty(value="商品SkuID", name="skuId")
    private Integer skuId;

    @ApiModelProperty(value="商品编码", name="skuNo")
    private String skuNo;

    @ApiModelProperty(value="购买数量", name="buyNum")
    private Integer buyNum;

    @ApiModelProperty(value="操作类型，+：增加，-：减少", name="action")
    private String action;

    @ApiModelProperty(value="挂单编码", name="hangNo")
    private String hangNo;

    @ApiModelProperty(value="下单会员ID", name="userId")
    private Integer userId;

}
