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
 * 店铺员工表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_staff")
@ApiModel(value = "MtStaff对象", description = "店铺员工表")
public class MtStaff implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("员工类别")
    private Integer category;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("微信号")
    private String wechat;

    @ApiModelProperty("对应的核销店铺id")
    private Integer storeId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("审核状态，A：审核通过；U：未审核；D：无效; ")
    private String auditedStatus;

    @ApiModelProperty("审核时间")
    private Date auditedTime;

    @ApiModelProperty("备注")
    private String description;

}
