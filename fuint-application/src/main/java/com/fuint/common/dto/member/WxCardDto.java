package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信会员卡实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class WxCardDto {

    @Schema(description="会员卡类型")
    private String cardType;

    @Schema(description="会员卡背景图")
    private String backgroundUrl;

    @Schema(description="商户logo")
    private String logoUrl;

    @Schema(description="商户名称")
    private String brandName;

    @Schema(description="Code展示类型")
    private String codeType;

    @Schema(description="卡券名，字数上限为9个汉字")
    private String title;

    @Schema(description="会员卡颜色")
    private String color;

    @Schema(description="卡券使用提醒，字数上限为16个汉字")
    private String notice;

    @Schema(description="卡券使用说明，字数上限为1024个汉字")
    private String description;

    @Schema(description="客服电话")
    private String servicePhone;

    @Schema(description="跳转外链的入口名字")
    private String customUrlName;

    @Schema(description="跳转外链的URL")
    private String customUrl;

    @Schema(description="显示在入口右侧的提示语")
    private String customUrlSubTitle;

    @Schema(description="卡券领取页面是否可分享")
    private Boolean canShare;

    @Schema(description="会员卡特权说明,限制1024汉字")
    private String prerogative;

    @Schema(description="显示积分")
    private Boolean supplyBonus;

    @Schema(description="跳转外链查看积分详情")
    private String bonusUrl;

    @Schema(description="积分规则")
    private String bonusRules;

    @Schema(description="是否支持储值")
    private Boolean supplyBalance;

    @Schema(description="跳转外链查看余额详情")
    private String balanceUrl;

}
