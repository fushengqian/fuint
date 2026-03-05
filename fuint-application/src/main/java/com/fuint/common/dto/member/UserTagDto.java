package com.fuint.common.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员标签DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserTagDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签ID")
    private Integer id;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("标签颜色")
    private String color;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("会员数量")
    private Integer userCount;
}
