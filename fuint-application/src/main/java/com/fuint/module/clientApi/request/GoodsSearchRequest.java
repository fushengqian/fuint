package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="当前页码", name="page")
    private Integer page;

    @ApiModelProperty(value="每页数量", name="pageSize")
    private Integer pageSize;

    @ApiModelProperty(value="商品名称", name="name")
    private String name;

    @ApiModelProperty(value="商品分类", name="cateId")
    private Integer cateId;

    @ApiModelProperty(value="排序类型", name="sortType")
    private String sortType;

    @ApiModelProperty(value="价格排序", name="sortPrice")
    private String sortPrice;

}
