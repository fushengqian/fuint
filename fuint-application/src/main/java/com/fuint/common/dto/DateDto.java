package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日期实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DateDto {

    @ApiModelProperty("开始时间")
    private String startDate;

    @ApiModelProperty("结束时间")
    private String endDate;
}
