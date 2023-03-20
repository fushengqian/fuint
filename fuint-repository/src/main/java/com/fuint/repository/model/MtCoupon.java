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
 * 卡券信息表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_coupon")
@ApiModel(value = "MtCoupon对象", description = "卡券信息表")
public class MtCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("券组ID")
    private Integer groupId;

    @ApiModelProperty("券类型，C优惠券；P储值卡；T计次卡")
    private String type;

    @ApiModelProperty("券名称")
    private String name;

    @ApiModelProperty("是否允许转赠")
    private Boolean isGive;

    @ApiModelProperty("获得卡券所消耗积分")
    private Integer point;

    @ApiModelProperty("适用商品：allGoods、parkGoods")
    private String applyGoods;

    @ApiModelProperty("领取码")
    private String receiveCode;

    @ApiModelProperty("使用专项")
    private String useFor;

    @ApiModelProperty("开始有效期")
    private Date beginTime;

    @ApiModelProperty("结束有效期")
    private Date endTime;

    @ApiModelProperty("面额")
    private BigDecimal amount;

    @ApiModelProperty("发放方式")
    private String sendWay;

    @ApiModelProperty("每次发放数量")
    private Integer sendNum;

    @ApiModelProperty("发行数量")
    private Integer total;

    @ApiModelProperty("每人拥有数量限制")
    private Integer limitNum;

    @ApiModelProperty("不可用日期，逗号隔开。周末：weekend；其他：2019-01-02_2019-02-09")
    private String exceptTime;

    @ApiModelProperty("所属店铺ID,逗号隔开")
    private String storeIds;

    @ApiModelProperty("适用会员等级,逗号隔开")
    private String gradeIds;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("效果图片")
    private String image;

    @ApiModelProperty("后台备注")
    private String remarks;

    @ApiModelProperty("获取券的规则")
    private String inRule;

    @ApiModelProperty("核销券的规则")
    private String outRule;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("A：正常；D：删除")
    private String status;


}
