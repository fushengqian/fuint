package com.fuint.common.dto;

import com.fuint.repository.model.MtUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 收银挂单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class HangUpDto {

    @ApiModelProperty("挂单号")
    private String hangNo;

    @ApiModelProperty("是否空白")
    private Boolean isEmpty;

    @ApiModelProperty("会员信息")
    private MtUser memberInfo;

    @ApiModelProperty("件数")
    private Integer num;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("时间")
    private String dateTime;

}
