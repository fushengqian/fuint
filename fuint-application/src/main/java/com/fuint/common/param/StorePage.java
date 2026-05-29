package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StorePage extends PageParam implements Serializable {

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("状态")
    private String status;

}
