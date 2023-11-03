package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券分组请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ReqCouponGroupDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("分组名称")
    private String name;

    @ApiModelProperty("价值金额")
    private BigDecimal money;

    @ApiModelProperty("发行数量")
    private Integer total;

    @ApiModelProperty("分组描述")
    private String description;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
