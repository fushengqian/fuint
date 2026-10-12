package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 售后分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundPage extends PageParam implements Serializable {

    @Schema(description="备注")
    private String remark;

    @Schema(description="状态")
    private String status;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="开始时间")
    private String startTime;

    @Schema(description="结束时间")
    private String endTime;
}
