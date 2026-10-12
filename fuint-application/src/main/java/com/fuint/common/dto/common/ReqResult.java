package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 请求返回结果
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ReqResult implements Serializable {

    @Schema(description="返回代码")
    private String code;

    @Schema(description="返回消息")
    private String msg;

    @Schema(description="返回结果")
    private boolean result;

    @Schema(description="返回数据")
    private Map<String, Object> data;

}
