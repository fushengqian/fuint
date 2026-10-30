package com.fuint.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 会员卡券导出
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserCouponExportVo {

    @ExcelProperty("核销二维码")
    private String code;

    @ExcelProperty("卡券ID")
    private String couponId;

    @ExcelProperty("卡券名称")
    private String couponName;

    @ExcelProperty("会员手机号")
    private String mobile;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("面额")
    private String amount;

    @ExcelProperty("余额")
    private String balance;
}
