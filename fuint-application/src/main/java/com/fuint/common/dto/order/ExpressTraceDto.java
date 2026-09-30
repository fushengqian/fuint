package com.fuint.common.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 物流轨迹明细
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ExpressTraceDto implements Serializable {

    @ApiModelProperty("轨迹时间(格式化)")
    private String time;

    @ApiModelProperty("轨迹时间(原始)")
    private String ftime;

    @ApiModelProperty("轨迹内容")
    private String context;

    @ApiModelProperty("物流状态")
    private String status;

    @ApiModelProperty("所在区域名称")
    private String areaName;

    @ApiModelProperty("所在区域编码")
    private String areaCode;
}
