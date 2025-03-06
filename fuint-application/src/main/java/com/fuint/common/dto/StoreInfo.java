package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Getter
@Setter
public class StoreInfo implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("商户号")
    private String merchantNo;

    @ApiModelProperty("商户名称")
    private String merchantName;

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("店铺二维码")
    private String qrCode;

    @ApiModelProperty("店铺LOGO")
    private String logo;

    @ApiModelProperty("是否默认店铺")
    private String isDefault;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("店铺地址")
    private String address;

    @ApiModelProperty("营业时间")
    private String hours;

    @ApiModelProperty("经度")
    private String latitude;

    @ApiModelProperty("纬度")
    private String longitude;

    @ApiModelProperty("距离")
    private BigDecimal distance;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("营业执照")
    private String license;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，1：正常；2：删除")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;

}
