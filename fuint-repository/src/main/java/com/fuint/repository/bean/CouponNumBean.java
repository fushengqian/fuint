package com.fuint.repository.bean;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 卡券数量对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@Schema(name="卡券数量对象", description="卡券数量对象")
public class CouponNumBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="数量")
    private Long num;

}
