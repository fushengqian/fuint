package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 请求收货地址请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AddressRequest implements Serializable {

    /**
     * 地址ID
     */
    @ApiModelProperty(value="收货地址ID", name="addressId")
    private Integer addressId;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value="收货人姓名", name="name")
    private String name;

    /**
     * 收货人手机号
     */
    @ApiModelProperty(value="收货人手机号", name="mobile")
    private String mobile;

    /**
     * 省份ID
     */
    @ApiModelProperty(value="省份ID", name="provinceId")
    private Integer provinceId;

    /**
     * 城市ID
     */
    @ApiModelProperty(value="城市ID", name="cityId")
    private Integer cityId;

    /**
     * 地区ID
     */
    @ApiModelProperty(value="地区ID", name="regionId")
    private Integer regionId;

    /**
     * 详细地址
     */
    @ApiModelProperty(value="详细地址", name="detail")
    private String detail;

    /**
     * 状态
     */
    @ApiModelProperty(value="状态", name="status")
    private String status;

    /**
     * 是否默认地址
     */
    @ApiModelProperty(value="是否默认地址", name="isDefault")
    private String isDefault;

}
