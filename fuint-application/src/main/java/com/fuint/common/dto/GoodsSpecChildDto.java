package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 商品规格子类实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecChildDto implements Serializable {

   @ApiModelProperty("自增ID")
   private Integer id;

   @ApiModelProperty("规格名称")
   private String name;

   @ApiModelProperty("是否选择")
   private boolean checked;

}

