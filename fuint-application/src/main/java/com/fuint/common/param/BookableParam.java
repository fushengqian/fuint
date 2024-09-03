package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="预约ID", name="bookId")
    private Integer bookId;

    @ApiModelProperty(value="预约日期", name="date")
    private String date;

    @ApiModelProperty(value="预约时间", name="time")
    private String time;

}
