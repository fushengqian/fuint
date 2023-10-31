package com.fuint.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 后台登录账号信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountInfo implements Serializable {

    @ApiModelProperty("账户主键id")
    private Integer id;

    @ApiModelProperty("账户编码")
    private String accountKey;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("状态 : 0 无效 1 有效")
    private int accountStatus;

    @ApiModelProperty("激活状态 : 0 未激活 1已激活")
    private String isActive;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyDate;

    @ApiModelProperty("随机码（公盐）")
    private String salt;

    @ApiModelProperty("所属角色ID")
    private String roleIds;

    @ApiModelProperty("是否被锁定")
    private int locked;

    @ApiModelProperty("从属对象")
    private int ownerId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属商户名称")
    private String merchantName;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺名称")
    private String storeName;

    @ApiModelProperty("关联员工ID")
    private Integer staffId;

    @ApiModelProperty("登录的Token")
    private String token;

}
