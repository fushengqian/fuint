package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 日期Dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class DayDto implements Serializable {

    @ApiModelProperty("星期")
    private String week;

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("是否可预订")
    private Boolean enable;
}
