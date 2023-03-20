package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员卡券表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_user_coupon")
@ApiModel(value = "MtUserCoupon对象", description = "会员卡券表")
public class MtUserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("券类型，C优惠券；P储值卡；T计次卡")
    private String type;

    @ApiModelProperty("效果图")
    private String image;

    @ApiModelProperty("券组ID")
    private Integer groupId;

    @ApiModelProperty("券ID")
    private Integer couponId;

    @ApiModelProperty("用户手机号码")
    private String mobile;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("使用店铺ID")
    private Integer storeId;

    @ApiModelProperty("面额")
    private BigDecimal amount;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取")
    private String status;

    @ApiModelProperty("使用时间")
    private Date usedTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("过期时间")
    private Date expireTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("导入UUID")
    private String uuid;

    @ApiModelProperty("订单ID")
    private Integer orderId;


}
