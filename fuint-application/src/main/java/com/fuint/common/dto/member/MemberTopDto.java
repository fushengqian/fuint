package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员排行DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberTopDto implements Serializable {

    @Schema(description="会员ID")
    private Integer id;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="消费金额")
    private BigDecimal amount;

    @Schema(description="购买数量")
    private Integer num;

}

