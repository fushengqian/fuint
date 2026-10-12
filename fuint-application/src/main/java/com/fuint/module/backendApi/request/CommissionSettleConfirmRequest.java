package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分佣提成提现确认参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionSettleConfirmRequest implements Serializable {

    @Schema(description="结算uuid", name="uuid")
    private String uuid;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="操作人", name="operator")
    private String operator;

}
