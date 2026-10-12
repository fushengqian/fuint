package com.fuint.module.clientApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="收货地址ID", name="addressId")
    private Integer addressId;

    @Schema(description="收货人姓名", name="name")
    private String name;

    @Schema(description="收货人手机号", name="mobile")
    private String mobile;

    @Schema(description="省份ID", name="provinceId")
    private Integer provinceId;

    @Schema(description="城市ID", name="cityId")
    private Integer cityId;

    @Schema(description="地区ID", name="regionId")
    private Integer regionId;

    @Schema(description="详细地址", name="detail")
    private String detail;

    @Schema(description="状态", name="status")
    private String status;

    @Schema(description="是否默认地址", name="isDefault")
    private String isDefault;

}
