package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 转赠记录分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GivePage extends PageParam implements Serializable {

    @Schema(description="状态，A正常；C取消")
    private String status;

    @Schema(description="获赠者会员ID")
    private Integer userId;

    @Schema(description="赠送者会员ID")
    private Integer giveUserId;

    @Schema(description="券ID")
    private String couponId;

    @Schema(description="获赠者手机号")
    private String mobile;

    @Schema(description="转赠者手机号")
    private String userMobile;

}
