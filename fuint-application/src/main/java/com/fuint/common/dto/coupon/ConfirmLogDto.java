package com.fuint.common.dto.coupon;

import com.alibaba.fastjson.annotation.JSONField;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 核销卡券流水dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ConfirmLogDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="核销编码")
    private String code;

    @Schema(description="核销状态")
    private String status;

    @Schema(description="会员卡券ID")
    private Integer userCouponId;

    @Schema(description="卡券信息")
    private MtCoupon couponInfo;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="核销店铺信息")
    private MtStore storeInfo;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Schema(description="创建时间")
    private Date createTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="核销金额")
    private BigDecimal amount;

    @Schema(description="核销uuid")
    private String uuid;

    @Schema(description="核销备注")
    private String remark;

    @Schema(description="最后操作人")
    private String operator;

}

