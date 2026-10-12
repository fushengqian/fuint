package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="标签ID")
    private Integer id;

    @Schema(description="标签名称")
    private String name;

    @Schema(description="标签颜色")
    private String color;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="标签描述")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="会员数量")
    private Integer userCount;

    @Schema(description="规则ID")
    private Integer ruleId;

    @Schema(description="规则名称")
    private String ruleName;
}
