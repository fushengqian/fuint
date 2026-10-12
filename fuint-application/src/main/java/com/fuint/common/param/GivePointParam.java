package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 积分转赠请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GivePointParam implements Serializable {

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="转赠数量", name="amount")
    private Integer amount;

    @Schema(description="转赠备注", name="remark")
    private String remark;

}
