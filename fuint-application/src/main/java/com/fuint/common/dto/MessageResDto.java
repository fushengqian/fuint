package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 短信发送返回实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class MessageResDto {

    @ApiModelProperty("发送ID")
    private String[] sendIds;

    @ApiModelProperty("发送结果")
    private Boolean result;

    @ApiModelProperty("短信ID")
    private String[] smsId;

}
