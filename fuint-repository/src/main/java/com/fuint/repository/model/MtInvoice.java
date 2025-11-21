package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票实体
 *
 * @Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_invoice")
@ApiModel(value = "invoice表对象", description = "invoice表对象")
public class MtInvoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("开票时间")
    private Date InvoiceTime;

    @ApiModelProperty("开票金额")
    private BigDecimal invoiceAmount;

    @ApiModelProperty("发票抬头")
    private String title;

    @ApiModelProperty("发票下载地址")
    private String downloadUrl;

    @ApiModelProperty("发票类型")
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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A开票中，B开票成功，C已冲红")
    private String status;

}
