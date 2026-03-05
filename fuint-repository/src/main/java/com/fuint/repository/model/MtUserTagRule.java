package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员标签规则实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_tag_rule")
@ApiModel(value = "MtUserTagRule对象", description = "会员标签规则表")
public class MtUserTagRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("关联标签ID")
    private Integer tagId;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则类型")
    private String ruleType;

    @ApiModelProperty("时间范围")
    private String timeRange;

    @ApiModelProperty("操作符")
    private String operatorType;

    @ApiModelProperty("阈值")
    private BigDecimal thresholdValue;

    @ApiModelProperty("最大值")
    private BigDecimal thresholdMax;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("是否自动执行")
    private String isAuto;

    @ApiModelProperty("优先级")
    private Integer priority;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;
}
