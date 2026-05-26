package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("会员卡券ID")
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("核销码")
    private String code;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
