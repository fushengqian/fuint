package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 分佣提成规则项目表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_commission_rule_item")
@Schema(name="MtCommissionRuleItem对象", description="分佣提成规则项目表")
public class MtCommissionRuleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="方案类型,goods:商品销售；coupon：卡券销售；recharge：会员充值")
    private String type;

    @Schema(description="分佣对象,member:会员分销；staff：员工提成")
    private String target;

    @Schema(description="规则ID")
    private Integer ruleId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="分佣对象ID")
    private Integer targetId;

    @Schema(description="提成方式（按比例/固定金额）")
    private String method;

    @Schema(description="适用店铺ID,逗号隔开")
    private String storeIds;

    @Schema(description="散客佣金")
    private BigDecimal guest;

    @Schema(description="二级散客佣金")
    private BigDecimal subGuest;

    @Schema(description="会员佣金")
    private BigDecimal member;

    @Schema(description="二级会员佣金")
    private BigDecimal subMember;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
