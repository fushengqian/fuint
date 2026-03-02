package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 发票分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class InvoicePage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("发票抬头")
    private String title;

    @ApiModelProperty("状态")
    private String status;
}
