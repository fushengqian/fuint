package com.fuint.common.dto.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 分销提成规则实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="规则名称")
    private String name;

    @Schema(description="方案类型,goods:商品销售；coupon：卡券销售；recharge：会员充值")
    private String type;

    @Schema(description="方案场景：commission 分佣提成；recommend 推荐奖励")
    private String scene;

    @Schema(description="注册奖励开关，Y：开；N：关")
    private String registerSwitch;

    @Schema(description="交易奖励开关，Y：开；N：关")
    private String orderSwitch;

    @Schema(description="交易奖励单笔实付门槛，0 表示不限")
    private BigDecimal orderAmount;

    @Schema(description="活动开始时间")
    private Date startTime;

    @Schema(description="活动结束时间")
    private Date endTime;

    @Schema(description="分佣对象,member:会员分销；staff：员工提成")
    private String target;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="适用店铺ID列表")
    private List<Integer> storeIdList;

    @Schema(description="具体项目列表")
    private List<CommissionRuleItemDto> detailList;

    @Schema(description="散客值")
    private BigDecimal visitorVal;

    @Schema(description="会员值")
    private BigDecimal memberVal;

    @Schema(description="二级散客值")
    private BigDecimal subVisitorVal;

    @Schema(description="二级会员值")
    private BigDecimal subMemberVal;

    @Schema(description="备注")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
