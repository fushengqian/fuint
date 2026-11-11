package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品评价表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_goods_comment")
@Schema(name="MtGoodsComment对象", description="商品评价表")
public class MtGoodsComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
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

    @Schema(description="评分1-5星")
    private Integer score;

    @Schema(description="评价内容")
    private String content;

    @Schema(description="评价图片，逗号分隔")
    private String images;

    @Schema(description="是否匿名 Y：是；N：否")
    private String isAnonymous;

    @Schema(description="审核状态 A：待审核；P：通过；R：拒绝")
    private String auditStatus;

    @Schema(description="商家回复内容")
    private String replyContent;

    @Schema(description="回复操作账号ID")
    private Integer replyAccountId;

    @Schema(description="回复时间")
    private Date replyTime;

    @Schema(description="状态 A：正常；D：删除")
    private String status;

    @Schema(description="备注")
    private String remark;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;
}
