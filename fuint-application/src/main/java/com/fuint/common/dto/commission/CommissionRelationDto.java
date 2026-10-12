package com.fuint.common.dto.commission;

import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="邀请码")
    private String inviteCode;

    @Schema(description="被邀请会员ID")
    private Integer subUserId;

    @Schema(description="会员信息")
    private MtUser subUserInfo;

    @Schema(description="等级")
    private Integer level;

    @Schema(description="备注")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
