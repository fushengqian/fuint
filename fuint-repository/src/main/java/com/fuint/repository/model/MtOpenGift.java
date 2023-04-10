package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员开卡赠礼
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_open_gift")
@ApiModel(value = "MtOpenGift对象", description = "会员开卡赠礼")
public class MtOpenGift implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("门店ID")
    private Integer storeId;

    @ApiModelProperty("会员等级ID")
    private Integer gradeId;

    @ApiModelProperty("赠送积分")
    private Integer point;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("卡券数量")
    private Integer couponNum;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;
}
