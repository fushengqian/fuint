package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 员工分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StaffPage extends PageParam implements Serializable {

    @Schema(description="类别")
    private Integer category;

    @Schema(description="真实姓名")
    private String realName;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="搜索关键字")
    private String keyword;

    @Schema(description="认证状态")
    private String auditedStatus;

}
