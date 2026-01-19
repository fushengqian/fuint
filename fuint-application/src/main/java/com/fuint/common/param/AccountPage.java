package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 帐号分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountPage extends PageParam implements Serializable {

    @ApiModelProperty("用户名")
    private String accountName;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("状态")
    private String accountStatus;

    @ApiModelProperty("状态")
    private Integer staffId;

}
