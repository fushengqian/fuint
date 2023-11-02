package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员排行DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class MemberTopDto implements Serializable {

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("消费金额")
    private BigDecimal amount;

    @ApiModelProperty("购买数量")
    private Integer num;

}

