package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 后台账号详情
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountInfoRequest implements Serializable {

    @ApiModelProperty(value="账号ID", name="id")
    private Integer id;

    @ApiModelProperty(value="用户名", name="accountName")
    private String accountName;

    @ApiModelProperty(value="密码", name="password")
    private String password;

    @ApiModelProperty(value="密码加密", name="salt")
    private String salt;

    @ApiModelProperty(value="状态", name="accountStatus")
    private String accountStatus;

    @ApiModelProperty(value="角色ID，逗号隔开", name="roleIds")
    private String roleIds;

    @ApiModelProperty(value="真实姓名", name="realName")
    private String realName;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="员工ID", name="staffId")
    private Integer staffId;
}
