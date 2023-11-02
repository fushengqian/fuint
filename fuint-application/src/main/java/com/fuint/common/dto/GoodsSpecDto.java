package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer specId;

    @ApiModelProperty("规格名称")
    private String name;

    @ApiModelProperty("规格值列表")
    private List<GoodsSpecValueDto> valueList;

}

