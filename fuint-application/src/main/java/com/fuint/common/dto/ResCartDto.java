package com.fuint.common.dto;

import com.fuint.repository.model.MtGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车返回DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ResCartDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("skuId")
    private Integer skuId;

    @ApiModelProperty("数量")
    private Double num;

    @ApiModelProperty("是否有效")
    private Boolean isEffect;

    @ApiModelProperty("商品规格")
    private List<GoodsSpecValueDto> specList;

    @ApiModelProperty("商品数据")
    private MtGoods goodsInfo;

}
