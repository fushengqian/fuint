package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 分佣提成规则项目表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_commission_rule_item")
@ApiModel(value = "MtCommissionRuleItem对象", description = "分佣提成规则项目表")
public class MtCommissionRuleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("方案类型,goods:商品销售；coupon：卡券销售；recharge：会员充值")
    private String type;

    @ApiModelProperty("分佣对象,member:会员分销；staff：员工提成")
    private String target;

    @ApiModelProperty("规则ID")
    private Integer ruleId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("分佣对象ID")
    private Integer targetId;

    @ApiModelProperty("提成方式（按比例/固定金额）")
    private String method;

    @ApiModelProperty("适用店铺ID,逗号隔开")
    private String storeIds;

    @ApiModelProperty("散客佣金")
    private BigDecimal guest;

    @ApiModelProperty("二级散客佣金")
    private BigDecimal subGuest;

    @ApiModelProperty("会员佣金")
    private BigDecimal member;

    @ApiModelProperty("二级会员佣金")
    private BigDecimal subMember;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
