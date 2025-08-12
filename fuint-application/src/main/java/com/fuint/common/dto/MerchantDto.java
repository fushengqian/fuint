package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商户实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("微信小程序appId")
    private String wxAppId;

    @ApiModelProperty("微信小程序秘钥")
    private String wxAppSecret;

    @ApiModelProperty("微信公众号appId")
    private String wxOfficialAppId;

    @ApiModelProperty("微信公众号秘钥")
    private String wxOfficialAppSecret;

    @ApiModelProperty("商户号")
    private String no;

    @ApiModelProperty("商户名称")
    private String name;

    @ApiModelProperty("商户logo")
    private String logo;

    @ApiModelProperty("联系人姓名")
    private String contact;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("联系地址")
    private String address;

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
