package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 请求参数实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ParamDto implements Serializable {

    @ApiModelProperty("参数键值")
    private String key;

    @ApiModelProperty("参数名称")
    private String name;

    @ApiModelProperty("参数值")
    private String value;

}
