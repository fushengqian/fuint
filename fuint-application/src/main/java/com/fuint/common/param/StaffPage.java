package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("类别")
    private Integer category;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("搜索关键字")
    private String keyword;

    @ApiModelProperty("认证状态")
    private String auditedStatus;

}
