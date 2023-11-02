package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 分组会员DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GroupMemberDto implements Serializable {

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("会员名称")
    private String mobile;
}

