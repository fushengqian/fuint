package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="token")
    private String token;

    @Schema(description="创建时间")
    private Long tokenCreatedTime;

    @Schema(description="失效时间")
    private Long tokenExpiryTime;

    @Schema(description="是否登录")
    private String isLogin;
}
