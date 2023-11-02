package com.fuint.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 转赠明细实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GiveItemDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("赠予对象手机号")
    private String mobile;

    @ApiModelProperty("用户手机")
    private String userMobile;

    @ApiModelProperty("分组ID")
    private Integer groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("卡券名称")
    private String couponName;

    @ApiModelProperty("总金额")
    private BigDecimal money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("赠送时间")
    private Date createTime;

}
