package com.fuint.common.dto.coupon;

import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的卡券DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MyCouponDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="卡券名称")
    private String name;

    @Schema(description="核销编码")
    private String code;

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

    @Schema(description="图片")
    private String image;

    @Schema(description="面额")
    private BigDecimal amount;

    @Schema(description="余额")
    private BigDecimal balance;

    @Schema(description="剩余次数")
    private Integer num;

    @Schema(description="是否可用")
    private boolean canUse;

    @Schema(description="有效期")
    private String effectiveDate;

    @Schema(description="提示信息")
    private String tips;

    @Schema(description="使用时间")
    private Date usedTime;

    @Schema(description="领券时间")
    private Date createTime;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="使用店铺")
    private MtStore storeInfo;

}
