package com.fuint.common.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格项实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsSpecItemDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="规格名称")
    private String name;

    @Schema(description="规格子类")
    private List<GoodsSpecChildDto> child;

}

