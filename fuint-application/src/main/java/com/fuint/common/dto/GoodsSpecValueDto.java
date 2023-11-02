package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 商品规格值实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecValueDto implements Serializable {

    @ApiModelProperty("值ID")
    private Integer specValueId;

    @ApiModelProperty("规格名")
    private String specName;

    @ApiModelProperty("规格值")
    private String specValue;

}

