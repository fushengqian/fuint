package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantPage extends PageParam implements Serializable {

    @Schema(description="商户ID")
    private String id;

    @Schema(description="商户名称")
    private String name;

    @Schema(description="状态")
    private String status;

}
