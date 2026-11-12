package com.fuint.common.dto.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品评价DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCommentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单商品ID")
    private Integer orderGoodsId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="商品名称")
    private String goodsName;

    @Schema(description="商品LOGO")
    private String goodsLogo;

    @Schema(description="评分1-5星")
    private Integer score;

    @Schema(description="评价内容")
    private String content;

    @Schema(description="评价图片")
    private List<String> images;

    @Schema(description="是否匿名 Y：是；N：否")
    private String isAnonymous;

    @Schema(description="审核状态 A：待审核；P：通过；R：拒绝")
    private String auditStatus;

    @Schema(description="商家回复内容")
    private String replyContent;

    @Schema(description="回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;

    @Schema(description="状态 A：正常；D：删除")
    private String status;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="展示的会员称呼，匿名时脱敏")
    private String userName;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description="更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
