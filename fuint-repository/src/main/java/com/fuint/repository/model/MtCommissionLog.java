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

    @Schema(description="记录场景：commission 分佣提成；register 注册奖励；order 交易奖励")
    private String scene;

    @Schema(description="奖励类型：commission 佣金；coupon 卡券；point 积分")
    private String rewardType;

    @Schema(description="分佣等级")
    private Integer level;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="被推荐人ID")
    private Integer inviteeUserId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="业务ID：注册=被推荐人user_id；交易=订单ID；历史佣金为NULL")
    private Integer bizId;

    @Schema(description="分佣金额")
    private BigDecimal amount;

    @Schema(description="奖励卡券ID")
    private Integer couponId;

    @Schema(description="奖励卡券张数")
    private Integer couponNum;

    @Schema(description="奖励积分")
    private Integer point;

    @Schema(description="券发放批次号 RECOMMEND_{logId}，退款时精确定位")
    private String couponUuid;

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

    @Schema(description="状态，A：待结算；B：已结算；C：已作废；D：已追回")
    private String status;

    @Schema(description="追回状态：0 无需；1 待追回；2 已追回；3 追回失败")
    private Integer clawbackStatus;

    @Schema(description="追回时间")
    private Date clawbackTime;

    @Schema(description="追回说明")
    private String clawbackRemark;

    @Schema(description="发放重试次数")
    private Integer retryCount;

    @Schema(description="最后重试时间")
    private Date lastRetryTime;

}
