package com.fuint.common.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="时间段")
    private String startTime;

    @Schema(description="时间段")
    private String endTime;

    @Schema(description="可预订数量")
    private String num;

}
