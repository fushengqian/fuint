package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 开通微信会员卡实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class OpenWxCardDto {

    @ApiModelProperty("会员编码")
    private String code;

    @ApiModelProperty("会员openId")
    private String openId;

    @ApiModelProperty("时间戳")
    private String timestamp;

    @ApiModelProperty("随机字符串")
    private String nonceStr;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("微信会员卡ID")
    private String cardId;

}
