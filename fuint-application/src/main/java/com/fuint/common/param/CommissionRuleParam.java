package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分销提成规则请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleParam implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("规则名称")
    private String name;

    @ApiModelProperty("分佣类型,member:会员分销；staff：员工提成")
    private String type;

    @ApiModelProperty("分佣对象")
    private String target;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("适用店铺ID列表")
    private List<Integer> storeIdList;

    @ApiModelProperty("具体项目列表")
    private List<CommissionRuleItemParam> detailList;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
