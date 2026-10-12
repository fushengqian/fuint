package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员等级请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserGradeParam implements Serializable {

    @Schema(description="会员等级ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="等级")
    private Integer grade;

    @Schema(description="等级名称")
    private String name;

    @Schema(description="升级会员等级条件描述")
    private String catchCondition;

    @Schema(description="升级会员等级条件，init:默认获取;pay:付费升级；frequency:消费次数；amount:累积消费金额升级")
    private String catchType;

    @Schema(description="达到升级条件的值")
    private BigDecimal catchValue;

    @Schema(description="会员权益描述")
    private String userPrivilege;

    @Schema(description="有效期")
    private Integer validDay;

    @Schema(description="享受折扣")
    private Float discount;

    @Schema(description="积分加速")
    private Float speedPoint;

    @Schema(description="返利比例")
    private Float rebate;

    @Schema(description="状态")
    private String status;

}
