package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GroupDataListDto {

    @ApiModelProperty("键值")
    private String key;

    @ApiModelProperty("数据")
    private GroupDataDto data;

}
