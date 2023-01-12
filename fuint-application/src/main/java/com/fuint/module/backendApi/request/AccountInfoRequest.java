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

    /**
     * 账号ID
     */
    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    /**
     * 用户名
     */
    @ApiModelProperty(value="用户名", name="accountName")
    private String accountName;

    /**
     * 密码
     */
    @ApiModelProperty(value="密码", name="password")
    private String password;

    /**
     * 密码加密
     */
    @ApiModelProperty(value="密码加密", name="salt")
    private String salt;

    /**
     * 状态
     */
    @ApiModelProperty(value="状态", name="accountStatus")
    private String accountStatus;

    /**
     * 角色ID，逗号隔开
     */
    @ApiModelProperty(value="角色ID", name="roleIds")
    private String roleIds;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value="真实姓名", name="realName")
    private String realName;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    /**
     * 员工ID
     */
    @ApiModelProperty(value="员工ID", name="staffId")
    private Integer staffId;
}
