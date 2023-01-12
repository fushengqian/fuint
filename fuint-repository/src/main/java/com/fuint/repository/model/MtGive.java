package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 转赠记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_give")
@ApiModel(value = "MtGive对象", description = "转赠记录表")
public class MtGive implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("获赠者用户ID")
    private Integer userId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("赠送者用户ID")
    private Integer giveUserId;

    @ApiModelProperty("赠予对象手机号")
    private String mobile;

    @ApiModelProperty("用户手机")
    private String userMobile;

    @ApiModelProperty("券组ID，逗号隔开")
    private String groupIds;

    @ApiModelProperty("券组名称，逗号隔开")
    private String groupNames;

    @ApiModelProperty("券ID，逗号隔开")
    private String couponIds;

    @ApiModelProperty("券名称，逗号隔开")
    private String couponNames;

    @ApiModelProperty("数量")
    private Integer num;

    @ApiModelProperty("总金额")
    private BigDecimal money;

    @ApiModelProperty("备注")
    private String note;

    @ApiModelProperty("留言")
    private String message;

    @ApiModelProperty("赠送时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A正常；C取消")
    private String status;


}
