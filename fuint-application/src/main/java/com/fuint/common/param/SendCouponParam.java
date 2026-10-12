package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 发放卡券请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SendCouponParam implements Serializable {

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="发放数量", name="num")
    private String num;

    @Schema(description="卡券ID", name="couponId")
    private String couponId;

    @Schema(description="会员ID", name="userIds")
    private String userIds;

    @Schema(description="发放对象", name="object")
    private String object;

}
