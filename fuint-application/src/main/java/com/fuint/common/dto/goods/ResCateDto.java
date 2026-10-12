package com.fuint.common.dto.goods;

import com.fuint.repository.model.MtGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 商品分类返回DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ResCateDto implements Serializable {

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="分类名称")
    private String name;

    @Schema(description="logo")
    private String logo;

    @Schema(description="商品列表")
    private List<MtGoods> goodsList;

    @Schema(description="排序")
    private Integer sort;

}
