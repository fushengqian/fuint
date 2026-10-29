package com.fuint.common.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 会员导出（表头与导入模板 MemberTemplate.xlsx 字段一致，不含密码）
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberExportVo {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("会员号")
    private String userNo;

    @ExcelProperty("身份证")
    private String idcard;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("手机号")
    private String mobile;

    @ExcelProperty("生日")
    private String birthday;

    @ExcelProperty("备注")
    private String description;

    @ExcelProperty("车牌")
    private String carNo;

    @ExcelProperty("会员等级")
    private String gradeName;

    @ExcelProperty("有效期")
    private String endTime;

    @ExcelProperty("积分")
    private String point;

    @ExcelProperty("余额")
    private String balance;

    @ExcelProperty("状态")
    private String status;
}
