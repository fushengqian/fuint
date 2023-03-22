package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员个人信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("称呼")
    private String name;

    @ApiModelProperty("微信open_id")
    private String openId;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("证件号码")
    private String idcard;

    @ApiModelProperty("等级ID")
    private String gradeId;

    @ApiModelProperty("等级名称")
    private String gradeName;

    @ApiModelProperty("会员开始时间")
    private Date startTime;

    @ApiModelProperty("会员结束时间")
    private Date endTime;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("积分")
    private Integer point;

    @ApiModelProperty("性别 0男；1女")
    private Integer sex;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("车牌号")
    private String carNo;

    @ApiModelProperty("来源渠道")
    private String source;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("默认店铺ID")
    private Integer storeId;

    @ApiModelProperty("默认店铺名称")
    private String storeName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最近登录时间")
    private String lastLoginTime;

    @ApiModelProperty("状态，A：激活；N：禁用；D：删除")
    private String status;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;
}
