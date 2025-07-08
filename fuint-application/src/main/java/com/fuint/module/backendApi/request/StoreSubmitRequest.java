package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 提交店铺信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StoreSubmitRequest implements Serializable {

    @ApiModelProperty(value="店铺ID", name="id")
    private Integer id;

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺名称", name="name")
    private String name;

    @ApiModelProperty(value="联系人", name="contact")
    private String contact;

    @ApiModelProperty(value="联系手机号", name="phone")
    private String phone;

    @ApiModelProperty(value="备注信息", name="description")
    private String description;

    @ApiModelProperty(value="是否默认店铺", name="isDefault")
    private String isDefault;

    @ApiModelProperty(value="店铺地址", name="address")
    private String address;

    @ApiModelProperty(value="营业时间", name="hours")
    private String hours;

    @ApiModelProperty(value="维度", name="latitude")
    private String latitude;

    @ApiModelProperty(value="经度", name="longitude")
    private String longitude;

    @ApiModelProperty(value="微信商户号", name="wxMchId")
    private String wxMchId;

    @ApiModelProperty(value="支付秘钥apiV2", name="wxApiV2")
    private String wxApiV2;

    @ApiModelProperty(value="微信支付证书", name="wxCertPath")
    private String wxCertPath;

    @ApiModelProperty(value="支付宝appId", name="alipayAppId")
    private String alipayAppId;

    @ApiModelProperty(value="支付宝私钥", name="alipayPrivateKey")
    private String alipayPrivateKey;

    @ApiModelProperty(value="支付宝公钥", name="alipayPublicKey")
    private String alipayPublicKey;

    @ApiModelProperty(value="店铺LOGO", name="logo")
    private String logo;

    @ApiModelProperty(value="营业执照", name="license")
    private String license;

    @ApiModelProperty(value="统一社会信用码", name="creditCode")
    private String creditCode;

    @ApiModelProperty(value="银行名称", name="bankName")
    private String bankName;

    @ApiModelProperty(value="银行户名", name="bankCardName")
    private String bankCardName;

    @ApiModelProperty(value="银行卡号", name="bankCardNo")
    private String bankCardNo;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
