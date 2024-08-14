package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookDetailParam implements Serializable {

    @ApiModelProperty(value="预约ID", name="bookId")
    private Integer bookId;

}
