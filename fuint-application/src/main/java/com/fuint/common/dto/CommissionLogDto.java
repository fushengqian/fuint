package com.fuint.common.dto;

import com.fuint.repository.model.MtCommissionRule;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销提成记录实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionLogDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("分佣类型")
    private String type;

    @ApiModelProperty("分佣类型名称")
    private String typeName;

    @ApiModelProperty("分佣等级")
    private Integer level;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("用户信息")
    private OrderUserDto userInfo;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("员工ID")
    private Integer staffId;

    @ApiModelProperty("所属店铺信息")
    private MtStaff staffInfo;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单信息")
    private MtOrder orderInfo;

    @ApiModelProperty("分佣金额")
    private BigDecimal amount;

    @ApiModelProperty("规则ID")
    private Integer ruleId;

    @ApiModelProperty("分佣规则信息")
    private MtCommissionRule ruleInfo;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("提现记录ID")
    private Integer cashId;

    @ApiModelProperty("最后操作人")
    private String isCash;

    @ApiModelProperty("提现时间")
    private Date cashTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

}
