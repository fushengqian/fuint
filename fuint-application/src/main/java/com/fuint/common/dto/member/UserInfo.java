package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="会员ID")
    private Integer id;

    @Schema(description="会员手机号")
    private String mobile;

    @Schema(description="登录Token")
    private String token;

}
