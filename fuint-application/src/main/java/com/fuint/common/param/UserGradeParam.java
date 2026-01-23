package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("会员等级ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("等级")
    private Integer grade;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("升级会员等级条件描述")
    private String catchCondition;

    @ApiModelProperty("升级会员等级条件，init:默认获取;pay:付费升级；frequency:消费次数；amount:累积消费金额升级")
    private String catchType;

    @ApiModelProperty("达到升级条件的值")
    private BigDecimal catchValue;

    @ApiModelProperty("会员权益描述")
    private String userPrivilege;

    @ApiModelProperty("有效期")
    private Integer validDay;

    @ApiModelProperty("享受折扣")
    private Float discount;

    @ApiModelProperty("积分加速")
    private Float speedPoint;

    @ApiModelProperty("返利比例")
    private Float rebate;

    @ApiModelProperty("状态")
    private String status;

}
