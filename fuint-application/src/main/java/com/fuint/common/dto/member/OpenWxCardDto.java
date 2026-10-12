package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 开通微信会员卡实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OpenWxCardDto {

    @Schema(description="会员编码")
    private String code;

    @Schema(description="会员openId")
    private String openId;

    @Schema(description="时间戳")
    private String timestamp;

    @Schema(description="随机字符串")
    private String nonceStr;

    @Schema(description="签名")
    private String signature;

    @Schema(description="微信会员卡ID")
    private String cardId;

}
