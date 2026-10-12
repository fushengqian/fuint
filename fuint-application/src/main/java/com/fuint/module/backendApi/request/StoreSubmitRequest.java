package com.fuint.module.backendApi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 提交店铺信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreSubmitRequest implements Serializable {

    @Schema(description="店铺ID", name="id")
    private Integer id;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺名称", name="name")
    private String name;

    @Schema(description="联系人", name="contact")
    private String contact;

    @Schema(description="联系手机号", name="phone")
    private String phone;

    @Schema(description="备注信息", name="description")
    private String description;

    @Schema(description="是否默认店铺", name="isDefault")
    private String isDefault;

    @Schema(description="店铺地址", name="address")
    private String address;

    @Schema(description="营业时间", name="hours")
    private String hours;

    @Schema(description="维度", name="latitude")
    private String latitude;

    @Schema(description="经度", name="longitude")
    private String longitude;

    @Schema(description="微信商户号", name="wxMchId")
    private String wxMchId;

    @Schema(description="支付秘钥apiV2", name="wxApiV2")
    private String wxApiV2;

    @Schema(description="微信支付证书", name="wxCertPath")
    private String wxCertPath;

    @Schema(description="支付宝appId", name="alipayAppId")
    private String alipayAppId;

    @Schema(description="支付宝私钥", name="alipayPrivateKey")
    private String alipayPrivateKey;

    @Schema(description="支付宝公钥", name="alipayPublicKey")
    private String alipayPublicKey;

    @Schema(description="店铺LOGO", name="logo")
    private String logo;

    @Schema(description="营业执照", name="license")
    private String license;

    @Schema(description="统一社会信用码", name="creditCode")
    private String creditCode;

    @Schema(description="银行名称", name="bankName")
    private String bankName;

    @Schema(description="银行户名", name="bankCardName")
    private String bankCardName;

    @Schema(description="银行卡号", name="bankCardNo")
    private String bankCardNo;

    @Schema(description="状态", name="status")
    private String status;

}
