package com.fuint.common.dto.message;

import com.fuint.common.dto.common.ParamDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 小程序订阅消息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SubMessageDto implements Serializable {

    @Schema(description="键值")
    private String key;

    @Schema(description="模板ID")
    private String templateId;

    @Schema(description="TID")
    private String tid;

    @Schema(description="标题")
    private String title;

    @Schema(description="内容")
    private String content;

    @Schema(description="状态")
    private String status;

    @Schema(description="模板参数")
    private List<ParamDto> params;
}
