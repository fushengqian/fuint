package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 邀请列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ShareListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="商户号", name="merchantNo")
    private String merchantNo;

}
