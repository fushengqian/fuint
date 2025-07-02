package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="会员名称", name="name")
    private String name;

    @ApiModelProperty(value="会员生日", name="birthday")
    private String birthday;

    @ApiModelProperty(value="头像url", name="avatar")
    private String avatar;

    @ApiModelProperty(value="性别", name="sex")
    private Integer sex;

    @ApiModelProperty(value="微信手机号授权码", name="code")
    private String code;

    @ApiModelProperty(value="微信手机号授权加密数据", name="encryptedData")
    private String encryptedData;

    @ApiModelProperty(value="微信手机号授权码iv", name="iv")
    private String iv;

    @ApiModelProperty(value="新密码", name="password")
    private String password;

    @ApiModelProperty(value="旧密码", name="passwordOld")
    private String passwordOld;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="验证码", name="verifyCode")
    private String verifyCode;

}
