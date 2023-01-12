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

    /**
     * 用户名
     */
    @ApiModelProperty(value="用户名", name="username")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value="密码", name="password")
    private String password;

    /**
     * 图形验证码
     */
    @ApiModelProperty(value="captchaCode", name="captchaCode")
    private String captchaCode;

    /**
     * 图形验证码uuid
     */
    @ApiModelProperty(value="uuid", name="uuid")
    private String uuid;
}
