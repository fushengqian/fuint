package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息体Body信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class Body implements Serializable {

    @ApiModelProperty("入参信息")
    private Map<String,Object> inParams;

    @ApiModelProperty("出参信息")
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
