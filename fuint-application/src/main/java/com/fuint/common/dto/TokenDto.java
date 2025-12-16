package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录Token实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class TokenDto implements Serializable {

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("创建时间")
    private Long tokenCreatedTime;

    @ApiModelProperty("失效时间")
    private Long tokenExpiryTime;

    @ApiModelProperty("是否登录")
    private String isLogin;
}
