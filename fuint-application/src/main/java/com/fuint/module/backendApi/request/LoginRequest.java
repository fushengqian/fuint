package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 后台登录请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class LoginRequest implements Serializable {

    @Schema(description="用户名", name="username")
    private String username;

    @Schema(description="密码", name="password")
    private String password;

    @Schema(description="captchaCode", name="captchaCode")
    private String captchaCode;

    @Schema(description="图形验证码uuid", name="uuid")
    private String uuid;
}
