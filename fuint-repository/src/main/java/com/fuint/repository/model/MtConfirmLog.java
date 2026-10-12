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
 * 核销记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_confirm_log")
@Schema(name="MtConfirmLog对象", description="核销记录表")
public class MtConfirmLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="编码")
    private String code;

    @Schema(description="核销金额")
    private BigDecimal amount;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="用户券ID")
    private Integer userCouponId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="卡券所属用户ID")
    private Integer userId;

    @Schema(description="核销者用户ID")
    private Integer operatorUserId;

    @Schema(description="核销店铺ID")
    private Integer storeId;

    @Schema(description="状态，A正常核销；D：撤销使用")
    private String status;

    @Schema(description="撤销时间")
    private Date cancelTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="操作来源user_id对应表t_account 还是 mt_user")
    private String operatorFrom;

    @Schema(description="备注信息")
    private String remark;


}
