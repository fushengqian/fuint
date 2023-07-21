package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private String id;

    @ApiModelProperty(value="会员ID", name="userId")
    private String userId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private String storeId;

    @ApiModelProperty(value="店铺ID，逗号分隔", name="storeIds")
    private String storeIds;

    @ApiModelProperty(value="订单状态", name="status")
    private String status;

    @ApiModelProperty(value="支付状态", name="payStatus")
    private String payStatus;

    @ApiModelProperty(value="数据类型，1）toPay：待支付；2）paid：已支付；3）cancel：已取消", name="dataType")
    private String dataType;

    @ApiModelProperty(value="订单类型", name="type")
    private String type;

    @ApiModelProperty(value="订单号", name="orderSn")
    private String orderSn;

    @ApiModelProperty(value="会员手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="订单模式，1）oneself：自取，2）express：配送", name="orderMode")
    private String orderMode;

    @ApiModelProperty(value="员工ID（销售人员）", name="staffId")
    private String staffId;

    @ApiModelProperty(value="卡券ID", name="couponId")
    private String couponId;

    @ApiModelProperty(value="时间类型", name="timeType")
    private String timeType;

    @ApiModelProperty(value="开始时间", name="startTime")
    private String startTime;

    @ApiModelProperty(value="结束时间", name="endTime")
    private String endTime;

}
