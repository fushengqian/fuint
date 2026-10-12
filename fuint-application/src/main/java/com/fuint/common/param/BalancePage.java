package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="备注信息")
    private String description;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
