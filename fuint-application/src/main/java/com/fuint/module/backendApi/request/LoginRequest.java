package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="用户名", name="username")
    private String username;

    @ApiModelProperty(value="密码", name="password")
    private String password;

    @ApiModelProperty(value="captchaCode", name="captchaCode")
    private String captchaCode;

    @ApiModelProperty(value="图形验证码uuid", name="uuid")
    private String uuid;
}
