package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 发票分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class InvoicePage extends PageParam implements Serializable {

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="发票抬头")
    private String title;

    @Schema(description="状态")
    private String status;
}
