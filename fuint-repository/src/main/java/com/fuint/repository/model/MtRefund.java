package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 售后表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_refund")
@Schema(name="MtRefund对象", description="售后表")
public class MtRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="退款金额")
    private BigDecimal amount;

    @Schema(description="售后类型")
    private String type;

    @Schema(description="退款备注")
    private String remark;

    @Schema(description="物流公司名称")
    private String expressName;

    @Schema(description="物流单号")
    private String expressNo;

    @Schema(description="拒绝原因")
    private String rejectReason;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态")
    private String status;

    @Schema(description="图片")
    private String images;

    @Schema(description="最后操作人")
    private String operator;

}
