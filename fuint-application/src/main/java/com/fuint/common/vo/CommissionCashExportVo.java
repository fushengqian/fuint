package com.fuint.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 佣金结算记录导出
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionCashExportVo {

    @ExcelProperty("结算单号")
    private String settleNo;

    @ExcelProperty("分佣对象")
    private String objectType;

    @ExcelProperty("姓名")
    private String userName;

    @ExcelProperty("手机号")
    private String mobile;

    @ExcelProperty("所属门店")
    private String storeName;

    @ExcelProperty("金额")
    private String amount;

    @ExcelProperty("状态")
    private String statusText;

    @ExcelProperty("付款方式")
    private String payTypeText;

    @ExcelProperty("申请时间")
    private String createTime;

    @ExcelProperty("打款时间")
    private String payTime;

    @ExcelProperty("操作人")
    private String operator;

    @ExcelProperty("备注")
    private String description;
}
