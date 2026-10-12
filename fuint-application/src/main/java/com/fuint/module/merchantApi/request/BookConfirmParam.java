package com.fuint.module.merchantApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="预约ID", name="bookId")
    private Integer bookId;

    @Schema(description="状态", name="status")
    private String status;

}
