package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 后台登录请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementRequest implements Serializable {

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="结算ID", name="settlementId")
    private Integer settlementId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="下单开始时间", name="startTime")
    private String startTime;

    @Schema(description="下单结束时间", name="endTime")
    private String endTime;

    @Schema(description="备注说明", name="remark")
    private String remark;

    @Schema(description="最后操作人", name="operator")
    private String operator;
}
