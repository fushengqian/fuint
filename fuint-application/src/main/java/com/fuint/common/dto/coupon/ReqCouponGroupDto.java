package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券分组请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ReqCouponGroupDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="分组名称")
    private String name;

    @Schema(description="价值金额")
    private BigDecimal money;

    @Schema(description="发行数量")
    private Integer total;

    @Schema(description="分组描述")
    private String description;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
