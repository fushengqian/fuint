package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PageParam implements Serializable {

    private static final long serialVersionUID = -1833130751169582924L;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="当前页数")
    private Integer page = 1;

    @Schema(description="分页大小")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    @Schema(description="排序字段", name="orderBy")
    private String orderBy;

    /**
     * 排序方式
     */
    @Schema(description="排序方式", name="order")
    private String order;
}

