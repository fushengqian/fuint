package com.fuint.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 转赠记录导出
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveLogExportVo {

    @ExcelProperty("记录ID")
    private String id;

    @ExcelProperty("用户手机号")
    private String userMobile;

    @ExcelProperty("转赠数量")
    private String num;

    @ExcelProperty("转赠总金额")
    private String money;

    @ExcelProperty("赠予对象手机号")
    private String mobile;

    @ExcelProperty("赠予时间")
    private String createTime;
}
