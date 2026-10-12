package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 日期Dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DayDto implements Serializable {

    @Schema(description="星期")
    private String week;

    @Schema(description="日期")
    private String date;

    @Schema(description="是否可预订")
    private Boolean enable;
}
