package com.fuint.common.dto.coupon;

import com.fuint.repository.model.MtConfirmLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 我的卡券实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserCouponDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="卡券名称")
    private String name;

    @Schema(description="卡券类型")
    private String type;

    @Schema(description="卡券内容")
    private Integer content;

    @Schema(description="状态")
    private String status;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="使用规则")
    private String useRule;

    @Schema(description="核销编码")
    private String code;

    @Schema(description="二维码")
    private String qrCode;

    @Schema(description="图片")
    private String image;

    @Schema(description="面额")
    private BigDecimal amount;

    @Schema(description="是否允许转赠")
    private Boolean isGive;

    @Schema(description="余额")
    private BigDecimal balance;

    @Schema(description="核销次数")
    private Integer confirmCount;

    @Schema(description="核销记录")
    private List<MtConfirmLog> confirmLogs;

    @Schema(description="是否可用(过期、状态等)")
    private boolean canUse;

    @Schema(description="有效期")
    private String effectiveDate;

    @Schema(description="适用店铺")
    private String storeNames;

    @Schema(description="提示信息")
    private String tips;

    @Schema(description="描述信息")
    private String description;

}
