package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserGradePage extends PageParam implements Serializable {

    @Schema(description="名称")
    private String name;

    @Schema(description="升级类型")
    private String catchType;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
