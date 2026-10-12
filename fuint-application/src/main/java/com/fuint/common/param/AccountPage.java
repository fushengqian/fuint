package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="用户名")
    private String accountName;

    @Schema(description="真实姓名")
    private String realName;

    @Schema(description="状态")
    private String accountStatus;

    @Schema(description="状态")
    private Integer staffId;

}
