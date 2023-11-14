package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="下单开始时间", name="startTime")
    private String startTime;

    @ApiModelProperty(value="下单结束时间", name="endTime")
    private String endTime;

    @ApiModelProperty(value="备注说明", name="remark")
    private String remark;

    @ApiModelProperty(value="最后操作人", name="operator")
    private String operator;
}
