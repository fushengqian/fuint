package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 后台账号详情
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountInfoRequest implements Serializable {

    @Schema(description="账号ID", name="id")
    private Integer id;

    @Schema(description="用户名", name="accountName")
    private String accountName;

    @Schema(description="密码", name="password")
    private String password;

    @Schema(description="密码加密", name="salt")
    private String salt;

    @Schema(description="状态", name="accountStatus")
    private String accountStatus;

    @Schema(description="角色ID，逗号隔开", name="roleIds")
    private String roleIds;

    @Schema(description="真实姓名", name="realName")
    private String realName;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="员工ID", name="staffId")
    private Integer staffId;
}
