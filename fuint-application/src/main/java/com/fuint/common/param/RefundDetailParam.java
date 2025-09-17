package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 售后订单详情请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundDetailParam implements Serializable {

    @ApiModelProperty(value="售后订单ID", name="refundId")
    private Integer refundId;

}
