package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提交商户请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantSubmitRequest implements Serializable {

    @ApiModelProperty(value="商户ID", name="id")
    private Integer id;

    @ApiModelProperty(value="商户名称", name="name")
    private String name;

    @ApiModelProperty(value="商户号", name="no")
    private String no;

    @ApiModelProperty(value="联系人", name="contact")
    private String contact;

    @ApiModelProperty(value="手机号", name="phone")
    private String phone;

    @ApiModelProperty(value="备注信息", name="description")
    private String description;

    @ApiModelProperty(value="地址", name="address")
    private String address;

    @ApiModelProperty(value="状态", name="status")
    private String status;

    @ApiModelProperty(value="图片", name="logo")
    private String logo;

    @ApiModelProperty(value="类型", name="type")
    private String type;

    @ApiModelProperty(value="小程序appId", name="wxAppId")
    private String wxAppId;

    @ApiModelProperty(value="小程序appSecret", name="wxAppSecret")
    private String wxAppSecret;

    @ApiModelProperty(value="公众号appId", name="wxOfficialAppId")
    private String wxOfficialAppId;

    @ApiModelProperty(value="公众号AppSecret", name="wxOfficialAppSecret")
    private String wxOfficialAppSecret;

    @ApiModelProperty(value="结算比例", name="settleRate")
    private BigDecimal settleRate;

}
