package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员登录信息实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserInfo implements Serializable {

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员手机号")
    private String mobile;

    @ApiModelProperty("登录Token")
    private String token;

}
