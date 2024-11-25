package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单物流信息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ExpressDto {

    @ApiModelProperty("物流公司名称")
    private String expressCompany;

    @ApiModelProperty("物流公司编码")
    private String expressCode;

    @ApiModelProperty("物流单号")
    private String expressNo;

    @ApiModelProperty("发货时间")
    private String expressTime;

}
