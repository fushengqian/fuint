package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class InvoiceParam implements Serializable {

    @Schema(description="ID", name="id")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="开票时间")
    private String invoiceTime;

    @Schema(description="开票金额")
    private BigDecimal invoiceAmount;

    @Schema(description="发票抬头")
    private String title;

    @Schema(description="发票下载地址")
    private String downloadUrl;

    @Schema(description="发票类型，普票、专票")
    private String type;

    @Schema(description="纳税人识别码")
    private String taxCode;

    @Schema(description="开户行")
    private String bankName;

    @Schema(description="开户卡号")
    private String bankCardNo;

    @Schema(description="开户户名")
    private String bankCardName;

    @Schema(description="开票备注")
    private String description;

    @Schema(description="接收邮箱")
    private String email;

    @Schema(description="联系电话")
    private String mobile;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A待开票，B开票中，C开票成功，D开票失败")
    private String status;

}
