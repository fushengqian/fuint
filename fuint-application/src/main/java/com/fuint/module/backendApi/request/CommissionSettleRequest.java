package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分佣提成结算请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionSettleRequest implements Serializable {

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="员工姓名", name="realName")
    private String realName;

    @Schema(description="员工手机号", name="mobile")
    private String mobile;

    @Schema(description="开始时间", name="startTime")
    private String startTime;

    @Schema(description="结束时间", name="endTime")
    private String endTime;

    @Schema(description="操作人", name="operator")
    private String operator;

}
