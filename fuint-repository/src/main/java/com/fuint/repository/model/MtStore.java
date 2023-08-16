package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 店铺表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_store")
@ApiModel(value = "MtStore对象", description = "店铺表")
public class MtStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("商户logo")
    private String logo;

    @ApiModelProperty("是否默认")
    private String isDefault;

    @ApiModelProperty("联系人姓名")
    private String contact;

    @ApiModelProperty("微信支付商户号")
    private String wxMchId;

    @ApiModelProperty("微信支付APIv2密钥")
    private String wxApiV2;

    @ApiModelProperty("支付宝appId")
    private String alipayAppId;

    @ApiModelProperty("支付宝应用私钥")
    private String alipayPrivateKey;

    @ApiModelProperty("支付宝应用公钥")
    private String alipayPublicKey;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("经度")
    private String latitude;

    @ApiModelProperty("维度")
    private String longitude;

    @ApiModelProperty("距离")
    private BigDecimal distance;

    @ApiModelProperty("营业时间")
    private String hours;

    @ApiModelProperty("营业执照")
    private String license;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行卡账户名")
    private String bankCardName;

    @ApiModelProperty("银行卡卡号")
    private String bankCardNo;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A：有效/启用；D：无效")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;

}
