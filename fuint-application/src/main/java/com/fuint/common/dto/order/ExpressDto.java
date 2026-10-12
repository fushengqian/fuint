package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单物流信息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ExpressDto {

    @Schema(description="物流公司名称")
    private String expressCompany;

    @Schema(description="物流公司编码")
    private String expressCode;

    @Schema(description="物流单号")
    private String expressNo;

    @Schema(description="发货时间")
    private String expressTime;

}
