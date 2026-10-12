package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 时间Dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class TimeDto implements Serializable {

    @Schema(description="时间段")
    private String time;

    @Schema(description="是否可预订")
    private Boolean enable;
}
