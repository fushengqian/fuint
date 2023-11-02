package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 个人资产实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class AssetDto {

    @ApiModelProperty("次卡数量")
    private Integer timer;

    @ApiModelProperty("储值卡数量")
    private Integer prestore;

    @ApiModelProperty("优惠券数量")
    private Integer coupon;
}
