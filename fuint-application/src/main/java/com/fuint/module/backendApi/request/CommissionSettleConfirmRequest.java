package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="结算uuid", name="uuid")
    private String uuid;

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="操作人", name="operator")
    private String operator;

}
