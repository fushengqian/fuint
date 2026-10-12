package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员卡券请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserCouponPage extends PageParam implements Serializable {

    @Schema(description="会员卡券ID")
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="核销码")
    private String code;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
