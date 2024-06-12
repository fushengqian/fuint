package com.fuint.common.vo.printer;

/**
 * 查询订单统计请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class QueryOrderStatisRequest extends RestRequest {

    /**
     * 打印机编号
     */
    private String sn;
    /**
     * 查询日期，格式YY-MM-DD，如：2016-09-20
     */
    private String date;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
