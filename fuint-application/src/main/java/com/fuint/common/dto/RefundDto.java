package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 售后实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class RefundDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("退款金额")
    private BigDecimal amount;

    @ApiModelProperty("售后类型")
    private String type;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("用户备注")
    private String remark;

    @ApiModelProperty("申请凭证图片")
    private List<String> imageList;

    @ApiModelProperty("申请凭证图片")
    private String images;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("订单详情")
    private UserOrderDto orderInfo;

    @ApiModelProperty("退货地址")
    private AddressDto address;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("状态说明")
    private String statusText;

}

