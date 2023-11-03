package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 小程序订阅消息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class SubMessageDto implements Serializable {

    @ApiModelProperty("键值")
    private String key;

    @ApiModelProperty("模板ID")
    private String templateId;

    @ApiModelProperty("TID")
    private String tid;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("模板参数")
    private List<ParamDto> params;
}
