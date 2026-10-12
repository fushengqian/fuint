package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员地址信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AddressDto implements Serializable {

    @Schema(description="账户主键ID")
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="会员手机号")
    private String mobile;

    @Schema(description="省份ID")
    private Integer provinceId;

    @Schema(description="省份名称")
    private String provinceName;

    @Schema(description="城市ID")
    private Integer cityId;

    @Schema(description="城市名称")
    private String cityName;

    @Schema(description="区ID")
    private Integer regionId;

    @Schema(description="区名称")
    private String regionName;

    @Schema(description="详细地址")
    private String detail;

    @Schema(description="是否默认地址")
    private String isDefault;

    @Schema(description="状态")
    private String status;
}
