package com.fuint.repository.bean;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店铺距离对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@ApiModel(value = "店铺距离对象", description = "店铺距离对象")
public class StoreDistanceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("店铺ID")
    private Integer id;

    @ApiModelProperty("数量")
    private String distance;

}
