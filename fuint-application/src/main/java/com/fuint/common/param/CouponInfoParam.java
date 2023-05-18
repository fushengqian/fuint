package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponInfoParam implements Serializable {

    @ApiModelProperty(value="卡券ID", name="couponId")
    private Integer couponId;

    @ApiModelProperty(value="会员卡券编码", name="userCouponCode")
    private String userCouponCode;

}
