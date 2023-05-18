package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderDetailParam implements Serializable {

    @ApiModelProperty(value="订单ID", name="orderId")
    private String orderId;

}
