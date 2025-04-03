package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 请求参数实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ParamDto implements Serializable {

    @ApiModelProperty("参数键值")
    private String key;

    @ApiModelProperty("参数名称")
    private String name;

    @ApiModelProperty("参数值")
    private String value;

    public ParamDto(String key, String name, String value) {
       this.key = key;
       this.name = name;
       this.value = value;
    }
}
