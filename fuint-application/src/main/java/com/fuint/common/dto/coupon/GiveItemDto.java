package com.fuint.common.dto.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 转赠明细实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveItemDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="赠予对象手机号")
    private String mobile;

    @Schema(description="用户手机")
    private String userMobile;

    @Schema(description="分组ID")
    private Integer groupId;

    @Schema(description="分组名称")
    private String groupName;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="卡券名称")
    private String couponName;

    @Schema(description="总金额")
    private BigDecimal money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="赠送时间")
    private Date createTime;

}
