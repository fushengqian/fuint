package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="发放数量", name="num")
    private String num;

    @ApiModelProperty(value="卡券ID", name="couponId")
    private String couponId;

    @ApiModelProperty(value="会员ID", name="userIds")
    private String userIds;

    @ApiModelProperty(value="发放对象", name="object")
    private String object;

}
