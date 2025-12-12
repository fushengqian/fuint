package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 余额明细分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BalancePage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
