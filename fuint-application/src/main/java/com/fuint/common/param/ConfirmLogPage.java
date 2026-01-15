package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券核销流水请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ConfirmLogPage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("手机号")
    private String mobile;

}
