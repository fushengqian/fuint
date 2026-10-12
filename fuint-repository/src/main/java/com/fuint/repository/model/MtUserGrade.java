package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员等级表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_grade")
@Schema(name="MtUserGrade对象", description="")
public class MtUserGrade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
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
