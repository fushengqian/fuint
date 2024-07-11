package com.fuint.common.dto;

import com.fuint.repository.model.MtUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 分销提成邀请记录实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRelationDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("会员信息")
    private MtUser userInfo;

    @ApiModelProperty("邀请码")
    private String inviteCode;

    @ApiModelProperty("被邀请会员ID")
    private Integer subUserId;

    @ApiModelProperty("会员信息")
    private MtUser subUserInfo;

    @ApiModelProperty("等级")
    private Integer level;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
