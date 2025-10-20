package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 库存商品实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Data
public class StockGoodsDto extends StoreInfo implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品数量")
    private Integer num;

    @ApiModelProperty("skuId")
    private Integer skuId;

}
