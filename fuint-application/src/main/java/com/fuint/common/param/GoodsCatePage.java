package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品分类分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCatePage extends PageParam implements Serializable {

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
