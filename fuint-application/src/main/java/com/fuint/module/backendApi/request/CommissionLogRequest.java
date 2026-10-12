package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分佣提成请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionLogRequest implements Serializable {

    @Schema(description="ID", name="id")
    private Integer id;

    @Schema(description="结算uuid", name="settleUuid")
    private String settleUuid;

    @Schema(description="分佣金额", name="amount")
    private String amount;

    @Schema(description="备注说明", name="remark")
    private String description;

    @Schema(description="状态", name="status")
    private String status;

    @Schema(description="最后操作人", name="operator")
    private String operator;
}
