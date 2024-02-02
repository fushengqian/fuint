package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    @ApiModelProperty(value="结算uuid", name="settleUuid")
    private String settleUuid;

    @ApiModelProperty(value="分佣金额", name="amount")
    private String amount;

    @ApiModelProperty(value="备注说明", name="remark")
    private String description;

    @ApiModelProperty(value="状态", name="status")
    private String status;

    @ApiModelProperty(value="最后操作人", name="operator")
    private String operator;
}
