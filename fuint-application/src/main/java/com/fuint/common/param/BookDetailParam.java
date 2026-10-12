package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookDetailParam implements Serializable {

    @Schema(description="预约ID", name="bookId")
    private Integer bookId;

}
