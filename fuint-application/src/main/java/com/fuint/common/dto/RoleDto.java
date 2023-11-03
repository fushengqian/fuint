package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色信息实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class RoleDto {

    @ApiModelProperty("账户主键ID")
    private Long id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("角色类型")
    private String type;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("状态 : A有效 D无效")
    private String status;

}
