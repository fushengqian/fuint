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
 * 店铺表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_store")
@Schema(name="MtStore对象", description="店铺表")
public class MtStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="店铺名称")
    private String name;

    @Schema(description="商户logo")
    private String logo;

    @Schema(description="店铺二维码")
    private String qrCode;

    @Schema(description="是否默认")
    private String isDefault;

    @Schema(description="联系人姓名")
    private String contact;

    @Schema(description="微信支付商户号")
    private String wxMchId;

    @Schema(description="微信支付APIv2密钥")
    private String wxApiV2;

    @Schema(description="微信支付证书")
    private String wxCertPath;

    @Schema(description="支付宝appId")
    private String alipayAppId;

    @Schema(description="支付宝应用私钥")
    private String alipayPrivateKey;

    @Schema(description="支付宝应用公钥")
    private String alipayPublicKey;

    @Schema(description="联系电话")
    private String phone;

    @Schema(description="地址")
    private String address;

    @Schema(description="经度")
    private String latitude;

    @Schema(description="维度")
    private String longitude;

    @Schema(description="距离")
    private BigDecimal distance;

    @Schema(description="营业时间")
    private String hours;

    @Schema(description="营业执照")
    private String license;

    @Schema(description="统一社会信用代码")
    private String creditCode;

    @Schema(description="银行名称")
    private String bankName;

    @Schema(description="银行卡账户名")
    private String bankCardName;

    @Schema(description="银行卡卡号")
    private String bankCardNo;

    @Schema(description="备注信息")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A：有效/启用；D：无效")
    private String status;

    @Schema(description="最后操作人")
    private String operator;

}
