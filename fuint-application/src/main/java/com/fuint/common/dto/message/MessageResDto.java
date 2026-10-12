package com.fuint.common.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短信发送返回实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MessageResDto {

    @Schema(description="发送ID")
    private String[] sendIds;

    @Schema(description="发送结果")
    private Boolean result;

    @Schema(description="短信ID")
    private String[] smsId;

}
