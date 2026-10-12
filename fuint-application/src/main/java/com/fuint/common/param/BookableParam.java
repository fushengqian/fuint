package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 可否预约请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookableParam implements Serializable {

    @Schema(description="预约ID", name="bookId")
    private Integer bookId;

    @Schema(description="用户ID", name="userId")
    private Integer userId;

    @Schema(description="预约日期", name="date")
    private String date;

    @Schema(description="预约时间", name="time")
    private String time;

}
