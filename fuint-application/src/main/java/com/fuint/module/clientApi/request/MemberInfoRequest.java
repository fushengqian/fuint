package com.fuint.module.clientApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberInfoRequest implements Serializable {

    @Schema(description="会员名称", name="name")
    private String name;

    @Schema(description="会员生日", name="birthday")
    private String birthday;

    @Schema(description="头像url", name="avatar")
    private String avatar;

    @Schema(description="性别", name="sex")
    private Integer sex;

    @Schema(description="微信手机号授权码", name="code")
    private String code;

    @Schema(description="微信手机号授权加密数据", name="encryptedData")
    private String encryptedData;

    @Schema(description="微信手机号授权码iv", name="iv")
    private String iv;

    @Schema(description="新密码", name="password")
    private String password;

    @Schema(description="旧密码", name="passwordOld")
    private String passwordOld;

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="验证码", name="verifyCode")
    private String verifyCode;

}
