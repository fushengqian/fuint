package com.fuint.common.dto;

import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的卡券DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class MyCouponDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("卡券名称")
    private String name;

    @ApiModelProperty("核销编码")
    private String code;

    @ApiModelProperty("卡券类型")
    private String type;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("使用规则")
    private String useRule;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("面额")
    private BigDecimal amount;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("剩余次数")
    private Integer num;

    @ApiModelProperty("是否可用")
    private boolean canUse;

    @ApiModelProperty("有效期")
    private String effectiveDate;

    @ApiModelProperty("提示信息")
    private String tips;

    @ApiModelProperty("使用时间")
    private Date usedTime;

    @ApiModelProperty("领券时间")
    private Date createTime;

    @ApiModelProperty("会员信息")
    private MtUser userInfo;

    @ApiModelProperty("使用店铺")
    private MtStore storeInfo;

}
