package com.fuint.common.dto.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券转赠实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="获赠者会员ID")
    private Integer userId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="赠送者会员ID")
    private Integer giveUserId;

    @Schema(description="获赠者手机号")
    private String mobile;

    @Schema(description="转赠者手机号")
    private String userMobile;

    @Schema(description="分组ID，逗号隔开")
    private String groupIds;

    @Schema(description="分组名称，逗号隔开")
    private String groupNames;

    @Schema(description="图片")
    private String image;

    @Schema(description="券ID，逗号隔开")
    private String couponIds;

    @Schema(description="券名称，逗号隔开")
    private String couponNames;

    @Schema(description="数量")
    private Integer num;

    @Schema(description="总金额")
    private BigDecimal money;

    @Schema(description="备注")
    private String note;

    @Schema(description="留言")
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="赠送时间")
    private String createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="更新时间")
    private String updateTime;

    @Schema(description="状态，A正常；C取消 ")
    private String status;

}

