package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券核销请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ConfirmParam implements Serializable {

    @Schema(description="核销码", name="code")
    private String code;

    @Schema(description="核销金额", name="amount")
    private String amount;

    @Schema(description="核销备注", name="remark")
    private String remark;

}
