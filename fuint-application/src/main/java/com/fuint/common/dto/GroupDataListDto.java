package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GroupDataListDto {

    @ApiModelProperty("键值")
    private String key;

    @ApiModelProperty("数据")
    private GroupDataDto data;

}
