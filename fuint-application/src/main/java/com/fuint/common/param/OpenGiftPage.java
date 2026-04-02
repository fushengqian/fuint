package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 开卡赠礼分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OpenGiftPage extends PageParam implements Serializable {

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("会员等级")
    private Integer gradeId;

    @ApiModelProperty("状态")
    private String status;
}
