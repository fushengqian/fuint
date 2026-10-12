package com.fuint.common.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格值实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsSpecValueDto implements Serializable {

    @Schema(description="值ID")
    private Integer specValueId;

    @Schema(description="规格名")
    private String specName;

    @Schema(description="规格值")
    private String specValue;

}

