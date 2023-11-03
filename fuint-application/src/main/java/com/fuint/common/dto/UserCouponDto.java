package com.fuint.common.dto;

import com.fuint.repository.model.MtConfirmLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 我的卡券实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class UserCouponDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("卡券名称")
    private String name;

    @ApiModelProperty("卡券类型")
    private String type;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("使用规则")
    private String useRule;

    @ApiModelProperty("核销编码")
    private String code;

    @ApiModelProperty("二维码")
    private String qrCode;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("面额")
    private BigDecimal amount;

    @ApiModelProperty("是否允许转赠")
    private Boolean isGive;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("核销次数")
    private Integer confirmCount;

    @ApiModelProperty("核销记录")
    private List<MtConfirmLog> confirmLogs;

    @ApiModelProperty("是否可用(过期、状态等)")
    private boolean canUse;

    @ApiModelProperty("有效期")
    private String effectiveDate;

    @ApiModelProperty("提示信息")
    private String tips;

    @ApiModelProperty("描述信息")
    private String description;

}
