package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券转赠请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveParam implements Serializable {

    @ApiModelProperty(value="转增对象手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="卡券ID，可逗号隔开", name="couponId")
    private String couponId;

    @ApiModelProperty(value="转赠备注", name="note")
    private String note;

    @ApiModelProperty(value="转赠留言", name="message")
    private String message;

    @ApiModelProperty(value="转赠人ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

}
