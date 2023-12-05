package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分销提成规则实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRuleDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("规则名称")
    private String name;

    @ApiModelProperty("方案类型,goods:商品销售；coupon：卡券销售；recharge：会员充值")
    private String type;

    @ApiModelProperty("分佣对象,member:会员分销；staff：员工提成")
    private String target;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("适用店铺ID列表")
    private List<Integer> storeIdList;

    @ApiModelProperty("具体项目列表")
    private List<CommissionRuleItemDto> detailList;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
