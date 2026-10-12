package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日期实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DateDto {

    @Schema(description="开始时间")
    private String startDate;

    @Schema(description="结束时间")
    private String endDate;
}
