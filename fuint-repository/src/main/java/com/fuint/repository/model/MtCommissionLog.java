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
 * 佣金记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_commission_log")
@Schema(name="MtCommissionLog对象", description="佣金记录表")
public class MtCommissionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="分佣对象,member:会员分销；staff：员工提成")
    private String target;

    @Schema(description="分佣类型")
    private String type;

    @Schema(description="分佣等级")
    private Integer level;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="分佣金额")
    private BigDecimal amount;

    @Schema(description="规则ID")
    private Integer ruleId;

    @Schema(description="规则项ID")
    private Integer ruleItemId;

    @Schema(description="备注")
    private String description;

    @Schema(description="结算uuid")
    private String settleUuid;

    @Schema(description="提现记录ID")
    private Integer cashId;

    @Schema(description="最后操作人")
    private String isCash;

    @Schema(description="提现时间")
    private Date cashTime;

    @Schema(description="是否提现")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A：待结算；B：已结算；C：已作废")
    private String status;

}
