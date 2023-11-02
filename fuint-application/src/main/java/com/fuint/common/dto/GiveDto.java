package com.fuint.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券转赠实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GiveDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("获赠者会员ID")
    private Integer userId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("赠送者会员ID")
    private Integer giveUserId;

    @ApiModelProperty("获赠者手机号")
    private String mobile;

    @ApiModelProperty("转赠者手机号")
    private String userMobile;

    @ApiModelProperty("分组ID，逗号隔开")
    private String groupIds;

    @ApiModelProperty("分组名称，逗号隔开")
    private String groupNames;

    @ApiModelProperty("图片")
    private String image;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("赠送时间")
    private String createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("状态，A正常；C取消 ")
    private String status;

}

