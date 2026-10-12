package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 消息体头信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class Head implements Serializable {

    @Schema(description="服务编号，用于确定唯一的服务")
    private String serviceId;

    @Schema(description="授权Token信息")
    private String token;

    @Schema(description="服务执行返回码(000000:正常)")
    private String returnCode;

    @Schema(description="服务执行返回信息")
    private String returnDesc;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Head{");
        sb.append("serviceId='").append(serviceId).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", returnCode='").append(returnCode).append('\'');
        sb.append(", returnDesc='").append(returnDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
