package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 店铺实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Getter
@Setter
public class StoreDto extends StoreInfo implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("微信商户号")
    private String wxMchId;

    @ApiModelProperty("微信支付秘钥")
    private String wxApiV2;

    @ApiModelProperty("微信支付证书")
    private String wxCertPath;

    @ApiModelProperty("支付宝appId")
    private String alipayAppId;

    @ApiModelProperty("支付宝应用私钥")
    private String alipayPrivateKey;

    @ApiModelProperty("支付宝支付公钥")
    private String alipayPublicKey;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行卡账户名")
    private String bankCardName;

    @ApiModelProperty("银行卡卡号")
    private String bankCardNo;

}
