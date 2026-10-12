package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券转赠请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveParam implements Serializable {

    @Schema(description="转增对象手机号", name="mobile")
    private String mobile;

    @Schema(description="卡券ID，可逗号隔开", name="couponId")
    private String couponId;

    @Schema(description="转赠备注", name="note")
    private String note;

    @Schema(description="转赠留言", name="message")
    private String message;

    @Schema(description="转赠人ID", name="userId")
    private Integer userId;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

}
