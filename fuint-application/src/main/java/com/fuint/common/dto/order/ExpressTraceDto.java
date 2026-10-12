package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="轨迹时间(格式化)")
    private String time;

    @Schema(description="轨迹时间(原始)")
    private String ftime;

    @Schema(description="轨迹内容")
    private String context;

    @Schema(description="物流状态")
    private String status;

    @Schema(description="所在区域名称")
    private String areaName;

    @Schema(description="所在区域编码")
    private String areaCode;
}
