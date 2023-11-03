package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 下单用户DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class OrderUserDto implements Serializable {

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员姓名")
    private String name;

    @ApiModelProperty("会员手机")
    private String mobile;

    @ApiModelProperty("证件类型")
    private String cardType;

    @ApiModelProperty("证件号")
    private String cardNo;

    @ApiModelProperty("地址")
    private String address;

}
