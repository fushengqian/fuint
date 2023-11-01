package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 会员地址信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class AddressDto implements Serializable {

    @ApiModelProperty("账户主键ID")
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("会员手机号")
    private String mobile;

    @ApiModelProperty("省份ID")
    private Integer provinceId;

    @ApiModelProperty("省份名称")
    private String provinceName;

    @ApiModelProperty("城市ID")
    private Integer cityId;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("区ID")
    private Integer regionId;

    @ApiModelProperty("区名称")
    private String regionName;

    @ApiModelProperty("详细地址")
    private String detail;

    @ApiModelProperty("是否默认地址")
    private String isDefault;

    @ApiModelProperty("状态")
    private String status;
}
