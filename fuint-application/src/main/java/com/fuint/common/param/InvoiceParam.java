package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("开票时间")
    private String invoiceTime;

    @ApiModelProperty("开票金额")
    private BigDecimal invoiceAmount;

    @ApiModelProperty("发票抬头")
    private String title;

    @ApiModelProperty("发票下载地址")
    private String downloadUrl;

    @ApiModelProperty("发票类型，普票、专票")
    private String type;

    @ApiModelProperty("纳税人识别码")
    private String taxCode;

    @ApiModelProperty("开户行")
    private String bankName;

    @ApiModelProperty("开户卡号")
    private String bankCardNo;

    @ApiModelProperty("开户户名")
    private String bankCardName;

    @ApiModelProperty("开票备注")
    private String description;

    @ApiModelProperty("接收邮箱")
    private String email;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A待开票，B开票中，C开票成功，D开票失败")
    private String status;

}
