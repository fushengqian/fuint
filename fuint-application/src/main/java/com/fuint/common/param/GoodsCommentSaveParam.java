package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 商品评价保存请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCommentSaveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="订单ID", name="orderId", requiredMode=Schema.RequiredMode.REQUIRED)
    private Integer orderId;

    @Schema(description="订单商品ID", name="orderGoodsId", requiredMode=Schema.RequiredMode.REQUIRED)
    private Integer orderGoodsId;

    @Schema(description="评分1-5星", name="score", requiredMode=Schema.RequiredMode.REQUIRED)
    private Integer score;

    @Schema(description="评价内容", name="content")
    private String content;

    @Schema(description="评价图片列表，最多6张", name="images")
    private List<String> images;

    @Schema(description="是否匿名 Y：是；N：否", name="isAnonymous")
    private String isAnonymous;
}
