package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员卡券表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_coupon")
@Schema(name="MtUserCoupon对象", description="会员卡券表")
public class MtUserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="核销码")
    private String code;

    @Schema(description="券类型，C优惠券；P储值卡；T计次卡")
    private String type;

    @Schema(description="效果图")
    private String image;

    @Schema(description="券组ID")
    private Integer groupId;

    @Schema(description="券ID")
    private Integer couponId;

    @Schema(description="用户手机号码")
    private String mobile;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="使用店铺ID")
    private Integer storeId;

    @Schema(description="面额")
    private BigDecimal amount;

    @Schema(description="余额")
    private BigDecimal balance;

    @Schema(description="状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取")
    private String status;

    @Schema(description="使用时间")
    private Date usedTime;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="过期时间")
    private Date expireTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="导入UUID")
    private String uuid;

    @Schema(description="订单ID")
    private Integer orderId;

}
