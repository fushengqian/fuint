package com.fuint.common.dto.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 店铺实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Data
public class StoreDto extends StoreInfo implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="微信商户号")
    private String wxMchId;

    @Schema(description="微信支付秘钥")
    private String wxApiV2;

    @Schema(description="微信支付证书")
    private String wxCertPath;

    @Schema(description="微信支付APIv3密钥")
    private String wxApiV3;

    @Schema(description="微信支付商户API证书序列号")
    private String wxCertSerialNo;

    @Schema(description="微信支付商户API私钥证书上传地址")
    private String wxPrivateKeyPath;

    @Schema(description="微信支付公钥证书上传地址")
    private String wxPayPublicKeyPath;

    @Schema(description="支付宝appId")
    private String alipayAppId;

    @Schema(description="支付宝应用私钥")
    private String alipayPrivateKey;

    @Schema(description="支付宝支付公钥")
    private String alipayPublicKey;

    @Schema(description="银行名称")
    private String bankName;

    @Schema(description="银行卡账户名")
    private String bankCardName;

    @Schema(description="银行卡卡号")
    private String bankCardNo;

}
