package com.fuint.common.vo.printer;

/**
 * 查询订单状态请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class QueryOrderStateRequest extends RestRequest {

    /**
     * 订单编号
     */
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
