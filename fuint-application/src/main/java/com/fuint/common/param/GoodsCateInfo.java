package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCateInfo implements Serializable {

    @Schema(description="分类ID")
    private Integer id;

    @Schema(description="名称")
    private String name;

    @Schema(description="描述信息")
    private String description;

    @Schema(description="分类LOGO")
    private String logo;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
