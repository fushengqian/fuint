package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 请求收货地址请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AddressRequest implements Serializable {

    @ApiModelProperty(value="收货地址ID", name="addressId")
    private Integer addressId;

    @ApiModelProperty(value="收货人姓名", name="name")
    private String name;

    @ApiModelProperty(value="收货人手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="省份ID", name="provinceId")
    private Integer provinceId;

    @ApiModelProperty(value="城市ID", name="cityId")
    private Integer cityId;

    @ApiModelProperty(value="地区ID", name="regionId")
    private Integer regionId;

    @ApiModelProperty(value="详细地址", name="detail")
    private String detail;

    @ApiModelProperty(value="状态", name="status")
    private String status;

    @ApiModelProperty(value="是否默认地址", name="isDefault")
    private String isDefault;

}
