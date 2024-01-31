package com.fuint.common.dto;

import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销提成提现实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionCashDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("结算单号")
    private String settleNo;

    @ApiModelProperty("结算uuid")
    private String uuid;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("用户信息")
    private OrderUserDto userInfo;

    @ApiModelProperty("员工ID")
    private Integer staffId;

    @ApiModelProperty("所属店铺信息")
    private MtStaff staffInfo;

    @ApiModelProperty("金额")
    private BigDecimal amount;

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
