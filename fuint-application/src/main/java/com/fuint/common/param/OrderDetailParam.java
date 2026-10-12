package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderDetailParam implements Serializable {

    @Schema(description="订单ID", name="orderId")
    private String orderId;

}
