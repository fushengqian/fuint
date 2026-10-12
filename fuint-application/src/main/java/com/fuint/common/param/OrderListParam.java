package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 订单列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderListParam extends PageParam implements Serializable {

    @Schema(description="ID", name="id")
    private String id;

    @Schema(description="关键字", name="keyword")
    private String keyword;

    @Schema(description="会员ID", name="userId")
    private Integer userId;

    @Schema(description="会员号", name="userCode")
    private String userCode;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="店铺ID，逗号分隔", name="storeIds")
    private String storeIds;

    @Schema(description="订单状态", name="status")
    private String status;

    @Schema(description="支付状态", name="payStatus")
    private String payStatus;

    @Schema(description="结算状态", name="settleStatus")
    private String settleStatus;

    @Schema(description="核销状态", name="confirmStatus")
    private String confirmStatus;

    @Schema(description="数据类型，1）toPay：待支付；2）paid：已支付；3）cancel：已取消", name="dataType")
    private String dataType;

    @Schema(description="支付类型", name="payType")
    private List<String> payType;

    @Schema(description="订单类型", name="type")
    private String type;

    @Schema(description="订单号", name="orderSn")
    private String orderSn;

    @Schema(description="会员手机号", name="mobile")
    private String mobile;

    @Schema(description="订单模式，1）oneself：自取，2）express：配送", name="orderMode")
    private String orderMode;

    @Schema(description="员工ID（销售人员）", name="staffId")
    private String staffId;

    @Schema(description="卡券ID", name="couponId")
    private String couponId;

    @Schema(description="时间类型", name="timeType")
    private String timeType;

    @Schema(description="开始时间", name="startTime")
    private String startTime;

    @Schema(description="结束时间", name="endTime")
    private String endTime;

}
