package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券核销请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ConfirmParam implements Serializable {

    @ApiModelProperty(value="核销码", name="code")
    private String code;

    @ApiModelProperty(value="核销金额", name="amount")
    private String amount;

    @ApiModelProperty(value="核销备注", name="remark")
    private String remark;

}
