package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约时段Dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookTimeDto implements Serializable {

    @ApiModelProperty("时间段")
    private String startTime;

    @ApiModelProperty("时间段")
    private String endTime;

    @ApiModelProperty("可预订数量")
    private String num;

}
