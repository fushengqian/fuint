package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品评价列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCommentListParam extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="商品ID", name="goodsId")
    private Integer goodsId;

    @Schema(description="商品名称", name="goodsName")
    private String goodsName;

    @Schema(description="订单号，模糊匹配", name="orderSn")
    private String orderSn;

    @Schema(description="会员号，模糊匹配", name="userNo")
    private String userNo;

    @Schema(description="评分筛选", name="score")
    private Integer score;

    @Schema(description="审核状态 A：待审核；P：通过；R：拒绝", name="auditStatus")
    private String auditStatus;

    @Schema(description="回复状态 Y：已回复；N：未回复", name="replyStatus")
    private String replyStatus;

    @Schema(description="评价状态 A：正常；D：删除", name="status")
    private String status;

    @Schema(description="开始时间，格式 yyyy-MM-dd", name="startTime")
    private String startTime;

    @Schema(description="结束时间，格式 yyyy-MM-dd", name="endTime")
    private String endTime;
}
