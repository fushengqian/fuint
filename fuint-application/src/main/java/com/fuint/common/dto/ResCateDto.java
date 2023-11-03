package com.fuint.common.dto;

import com.fuint.repository.model.MtGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类返回DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ResCateDto implements Serializable {

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("商品列表")
    private List<MtGoods> goodsList;

}
