package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 售后订单详情请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundDetailParam implements Serializable {

    @Schema(description="售后订单ID", name="refundId")
    private Integer refundId;

    @Schema(description="状态", name="status")
    private String status;

    @Schema(description="备注信息", name="remark")
    private String remark;

}
