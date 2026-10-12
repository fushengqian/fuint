package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券领取请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponReceiveParam implements Serializable {

    @Schema(description="卡券ID", name="couponId")
    private Integer couponId;

    @Schema(description="领取数量", name="num")
    private Integer num;

    @Schema(description="会员ID", name="userId")
    private Integer userId;

    @Schema(description="领取码", name="receiveCode")
    private String receiveCode;

}
