package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCateInfo implements Serializable {

    @ApiModelProperty("分类ID")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("分类LOGO")
    private String logo;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
