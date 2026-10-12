package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员个人信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user")
@Schema(name="MtUser对象", description="会员个人信息")
public class MtUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="会员ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="头像")
    private String avatar;

    @Schema(description="分组ID")
    private Integer groupId;

    @Schema(description="称呼")
    private String name;

    @Schema(description="微信open_id")
    private String openId;

    @Schema(description="手机号码")
    private String mobile;

    @Schema(description="证件号码")
    private String idcard;

    @Schema(description="等级ID")
    private Integer gradeId;

    @Schema(description="会员开始时间")
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Schema(description="会员结束时间")
    @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @Schema(description="余额")
    private BigDecimal balance;

    @Schema(description="积分")
    private Integer point;

    @Schema(description="性别 1男；0女")
    private Integer sex;

    @Schema(description="出生日期")
    private String birthday;

    @Schema(description="车牌号")
    private String carNo;

    @Schema(description="来源渠道")
    private String source;

    @Schema(description="密码")
    private String password;

    @Schema(description="salt")
    private String salt;

    @Schema(description="地址")
    private String address;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="默认店铺")
    private Integer storeId;

    @Schema(description="是否员工")
    private String isStaff;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A：激活；N：禁用；D：删除")
    private String status;

    @Schema(description="是否已完善资料 Y：是；N：否")
    private String profileCompleted;

    @Schema(description="备注信息")
    private String description;

    @Schema(description="注册IP")
    private String ip;

    @Schema(description="最后操作人")
    private String operator;
}
