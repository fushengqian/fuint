package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name="MtUserTagRule对象", description="会员标签规则表")
public class MtUserTagRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="关联标签ID")
    private Integer tagId;

    @Schema(description="规则名称")
    private String ruleName;

    @Schema(description="规则类型")
    private String ruleType;

    @Schema(description="时间范围")
    private String timeRange;

    @Schema(description="操作符")
    private String operatorType;

    @Schema(description="阈值")
    private BigDecimal thresholdValue;

    @Schema(description="最大值")
    private BigDecimal thresholdMax;

    @Schema(description="规则描述")
    private String description;

    @Schema(description="是否自动执行")
    private String isAuto;

    @Schema(description="优先级")
    private Integer priority;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;
}
