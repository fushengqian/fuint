package com.fuint.common.dto.order;

import com.fuint.common.dto.goods.GoodsSpecValueDto;
import com.fuint.repository.model.MtGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车返回DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ResCartDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="skuId")
    private Integer skuId;

    @Schema(description="数量")
    private Double num;

    @Schema(description="是否有效")
    private Boolean isEffect;

    @Schema(description="商品规格")
    private List<GoodsSpecValueDto> specList;

    @Schema(description="商品数据")
    private MtGoods goodsInfo;

}
