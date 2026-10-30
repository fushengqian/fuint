package com.fuint.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 订单导出
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderExportVo {

    @ExcelProperty("订单号")
    private String orderSn;

    @ExcelProperty("会员名称")
    private String userName;

    @ExcelProperty("手机号")
    private String mobile;

    @ExcelProperty("订单类型")
    private String typeName;

    @ExcelProperty("配送方式")
    private String orderMode;

    @ExcelProperty("所属门店")
    private String storeName;

    @ExcelProperty("总金额")
    private String amount;

    @ExcelProperty("支付状态")
    private String payStatus;

    @ExcelProperty("订单状态")
    private String statusText;

    @ExcelProperty("支付金额")
    private String payAmount;

    @ExcelProperty("优惠金额")
    private String discount;

    @ExcelProperty("积分金额")
    private String pointAmount;

    @ExcelProperty("关联员工")
    private String staffName;

    @ExcelProperty("下单时间")
    private String createTime;

    @ExcelProperty("更新时间")
    private String updateTime;
}
