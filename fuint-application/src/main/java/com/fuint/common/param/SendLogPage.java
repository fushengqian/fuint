package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 发券记录分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SendLogPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("手机号")
    private String mobile;
}
