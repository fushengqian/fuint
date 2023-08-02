package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商户表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_merchant")
@ApiModel(value = "MtMerchant对象", description = "商户表")
public class MtMerchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A：有效/启用；D：无效")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;

}
