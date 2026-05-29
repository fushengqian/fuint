package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("状态，A正常；C取消")
    private String status;

    @ApiModelProperty("获赠者会员ID")
    private Integer userId;

    @ApiModelProperty("赠送者会员ID")
    private Integer giveUserId;

    @ApiModelProperty("券ID")
    private String couponId;

    @ApiModelProperty("获赠者手机号")
    private String mobile;

    @ApiModelProperty("转赠者手机号")
    private String userMobile;

}
