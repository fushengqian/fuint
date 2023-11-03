package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Map;

/**
 * 请求返回结果
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ReqResult implements Serializable {

    @ApiModelProperty("返回代码")
    private String code;

    @ApiModelProperty("返回消息")
    private String msg;

    @ApiModelProperty("返回结果")
    private boolean result;

    @ApiModelProperty("返回数据")
    private Map<String, Object> data;

}
