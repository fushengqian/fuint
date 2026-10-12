package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分销提成规则请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleParam implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="规则名称")
    private String name;

    @Schema(description="分佣类型,member:会员分销；staff：员工提成")
    private String type;

    @Schema(description="分佣对象")
    private String target;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="适用店铺ID列表")
    private List<Integer> storeIdList;

    @Schema(description="具体项目列表")
    private List<CommissionRuleItemParam> detailList;

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

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
