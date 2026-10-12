package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息体Body信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class Body implements Serializable {

    @Schema(description="入参信息")
    private Map<String,Object> inParams;

    @Schema(description="出参信息")
    private Map<String,Object> outParams;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Body{");
        sb.append("inParams=").append(inParams);
        sb.append(", outParams=").append(outParams);
        sb.append('}');
        return sb.toString();
    }
}
