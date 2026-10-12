package com.fuint.common.dto.order;

import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 售后实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="退款金额")
    private BigDecimal amount;

    @Schema(description="售后类型")
    private String type;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="用户备注")
    private String remark;

    @Schema(description="物流公司名称")
    private String expressName;

    @Schema(description="物流单号")
    private String expressNo;

    @Schema(description="拒绝原因")
    private String rejectReason;

    @Schema(description="申请凭证图片")
    private List<String> imageList;

    @Schema(description="申请凭证图片")
    private String images;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="订单详情")
    private UserOrderDto orderInfo;

    @Schema(description="退货地址")
    private AddressDto address;

    @Schema(description="店铺信息")
    private MtStore storeInfo;

    @Schema(description="创建时间")
    private String createTime;

    @Schema(description="更新时间")
    private String updateTime;

    @Schema(description="状态")
    private String status;

    @Schema(description="状态说明")
    private String statusText;

}

