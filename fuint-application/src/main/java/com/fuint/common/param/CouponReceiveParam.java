package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券领取请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponReceiveParam implements Serializable {

    @ApiModelProperty(value="卡券ID", name="couponId")
    private Integer couponId;

    @ApiModelProperty(value="领取数量", name="num")
    private Integer num;

    @ApiModelProperty(value="会员ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="领取码", name="receiveCode")
    private String receiveCode;

}
