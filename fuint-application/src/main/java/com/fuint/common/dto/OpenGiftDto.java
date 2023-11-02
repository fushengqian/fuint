package com.fuint.common.dto;

import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUserGrade;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开卡赠礼实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class OpenGiftDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("会员等级信息")
    private MtUserGrade gradeInfo;

    @ApiModelProperty("赠送积分")
    private Integer point;

    @ApiModelProperty("卡券信息")
    private MtCoupon couponInfo;

    @ApiModelProperty("卡券数量")
    private Integer couponNum;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;

}

