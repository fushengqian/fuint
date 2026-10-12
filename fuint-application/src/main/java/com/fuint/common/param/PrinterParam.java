package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PrinterParam implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="打印机编号")
    private String sn;

    @Schema(description="打印机类型，order：订单小票，label：标签小票")
    private String type;

    @Schema(description="打印机名称")
    private String name;

    @Schema(description="是否自动打印机")
    private String autoPrint;

    @Schema(description="支付前打印")
    private String beforePay;

    @Schema(description="支付后打印")
    private String afterPay;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
