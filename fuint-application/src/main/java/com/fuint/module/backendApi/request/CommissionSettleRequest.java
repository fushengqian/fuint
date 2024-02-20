package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="员工姓名", name="realName")
    private String realName;

    @ApiModelProperty(value="员工手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="开始时间", name="startTime")
    private String startTime;

    @ApiModelProperty(value="结束时间", name="endTime")
    private String endTime;

    @ApiModelProperty(value="操作人", name="operator")
    private String operator;

}
