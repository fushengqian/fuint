package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 修改后台角色状态请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class DutyStatusRequest implements Serializable {

    @Schema(description="角色ID", name="roleId")
    private Integer roleId;

    @Schema(description="状态", name="status")
    private String status;

}
