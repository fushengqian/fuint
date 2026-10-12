package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发票实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_invoice")
@Schema(name="invoice表对象", description="invoice表对象")
public class MtInvoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="开票时间")
    private Date InvoiceTime;

    @Schema(description="开票金额")
    private BigDecimal invoiceAmount;

    @Schema(description="发票抬头")
    private String title;

    @Schema(description="发票下载地址")
    private String downloadUrl;

    @Schema(description="发票类型")
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

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A开票中，B开票成功，C已冲红")
    private String status;

}
