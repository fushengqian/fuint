package com.fuint.module.merchantApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约确认请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookConfirmParam implements Serializable {

    @ApiModelProperty(value="预约ID", name="bookId")
    private Integer bookId;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
