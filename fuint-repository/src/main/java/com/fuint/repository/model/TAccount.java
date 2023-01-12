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
 * 后台管理员表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_account")
@ApiModel(value = "TAccount对象", description = "后台管理员表")
public class TAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "acct_id", type = IdType.AUTO)
    private Integer acctId;

    @ApiModelProperty("账户编码")
    private String accountKey;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("0 无效 1 有效")
    private Integer accountStatus;

    @ApiModelProperty("0 未激活 1已激活")
    private Integer isActive;

    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("修改时间")
    private Date modifyDate;

    @ApiModelProperty("随机码")
    private String salt;

    private String roleIds;

    private Integer locked;

    @ApiModelProperty("所属平台")
    private Integer ownerId;

    private String realName;

    @ApiModelProperty("管辖店铺id  : -1 代表全部")
    private Integer storeId;

    @ApiModelProperty("管辖店铺名称")
    private String storeName;

    @ApiModelProperty("员工ID")
    private Integer staffId;
}
