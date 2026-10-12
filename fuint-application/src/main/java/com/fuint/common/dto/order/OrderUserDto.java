package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 下单用户DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderUserDto implements Serializable {

    @Schema(description="会员ID")
    private Integer id;

    @Schema(description="会员号")
    private String no;

    @Schema(description="会员姓名")
    private String name;

    @Schema(description="会员手机")
    private String mobile;

    @Schema(description="证件类型")
    private String cardType;

    @Schema(description="证件号")
    private String cardNo;

    @Schema(description="地址")
    private String address;

    @Schema(description="openId")
    private String openId;

}
