package com.fuint.module.clientApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品查询请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsSearchRequest implements Serializable {

    @Schema(description="当前页码", name="page")
    private Integer page;

    @Schema(description="每页数量", name="pageSize")
    private Integer pageSize;

    @Schema(description="商品名称", name="name")
    private String name;

    @Schema(description="商品分类", name="cateId")
    private Integer cateId;

    @Schema(description="排序类型", name="sortType")
    private String sortType;

    @Schema(description="价格排序", name="sortPrice")
    private String sortPrice;

}
