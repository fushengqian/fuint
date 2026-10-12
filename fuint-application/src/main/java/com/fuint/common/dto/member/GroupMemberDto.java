package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分组会员DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GroupMemberDto implements Serializable {

    @Schema(description="会员ID")
    private Integer id;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="会员名称")
    private String mobile;
}

