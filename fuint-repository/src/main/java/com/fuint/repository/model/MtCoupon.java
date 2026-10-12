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
 * 卡券信息表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_coupon")
@Schema(name="MtCoupon对象", description="卡券信息表")
public class MtCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="券组ID")
    private Integer groupId;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="券类型，C优惠券；P储值卡；T计次卡")
    private String type;

    @Schema(description="券内容，如：0表示满减券、1表示折扣券")
    private Integer content;

    @Schema(description="券名称")
    private String name;

    @Schema(description="是否允许转赠")
    private Boolean isGive;

    @Schema(description="获得卡券所消耗积分")
    private Integer point;

    @Schema(description="适用商品：allGoods、parkGoods")
    private String applyGoods;

    @Schema(description="领取码")
    private String receiveCode;

    @Schema(description="使用专项")
    private String useFor;

    @Schema(description="过期类型")
    private String expireType;

    @Schema(description="有效天数")
    private Integer expireTime;

    @Schema(description="开始有效期")
    private Date beginTime;

    @Schema(description="结束有效期")
    private Date endTime;

    @Schema(description="面额")
    private BigDecimal amount;

    @Schema(description="发放方式")
    private String sendWay;

    @Schema(description="每次发放数量")
    private Integer sendNum;

    @Schema(description="发行数量")
    private Integer total;

    @Schema(description="每人拥有数量限制")
    private Integer limitNum;

    @Schema(description="不可用日期，逗号隔开。周末：weekend；其他：2019-01-02_2019-02-09")
    private String exceptTime;

    @Schema(description="适用店铺ID,逗号隔开")
    private String storeIds;

    @Schema(description="适用会员等级,逗号隔开")
    private String gradeIds;

    @Schema(description="描述信息")
    private String description;

    @Schema(description="效果图片")
    private String image;

    @Schema(description="后台备注")
    private String remarks;

    @Schema(description="获取券的规则")
    private String inRule;

    @Schema(description="核销券的规则")
    private String outRule;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="A：正常；D：删除")
    private String status;


}
