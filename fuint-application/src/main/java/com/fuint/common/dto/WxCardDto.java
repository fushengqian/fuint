package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信会员卡实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class WxCardDto {

    @ApiModelProperty("会员卡类型")
    private String cardType;

    @ApiModelProperty("会员卡背景图")
    private String backgroundUrl;

    @ApiModelProperty("商户logo")
    private String logoUrl;

    @ApiModelProperty("商户名称")
    private String brandName;

    @ApiModelProperty("Code展示类型")
    private String codeType;

    @ApiModelProperty("卡券名，字数上限为9个汉字")
    private String title;

    @ApiModelProperty("会员卡颜色")
    private String color;

    @ApiModelProperty("卡券使用提醒，字数上限为16个汉字")
    private String notice;

    @ApiModelProperty("卡券使用说明，字数上限为1024个汉字")
    private String description;

    @ApiModelProperty("客服电话")
    private String servicePhone;

    @ApiModelProperty("跳转外链的入口名字")
    private String customUrlName;

    @ApiModelProperty("跳转外链的URL")
    private String customUrl;

    @ApiModelProperty("显示在入口右侧的提示语")
    private String customUrlSubTitle;

    @ApiModelProperty("卡券领取页面是否可分享")
    private Boolean canShare;

    @ApiModelProperty("会员卡特权说明,限制1024汉字")
    private String prerogative;

    @ApiModelProperty("显示积分")
    private Boolean supplyBonus;

    @ApiModelProperty("跳转外链查看积分详情")
    private String bonusUrl;

    @ApiModelProperty("积分规则")
    private String bonusRules;

    @ApiModelProperty("是否支持储值")
    private Boolean supplyBalance;

    @ApiModelProperty("跳转外链查看余额详情")
    private String balanceUrl;

}
