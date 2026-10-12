package com.fuint.common.dto.cashier;

import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收银挂单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class HangUpDto {

    @Schema(description="挂单号")
    private String hangNo;

    @Schema(description="是否空白")
    private Boolean isEmpty;

    @Schema(description="会员信息")
    private MtUser memberInfo;

    @Schema(description="件数")
    private Double num;

    @Schema(description="金额")
    private BigDecimal amount;

    @Schema(description="时间")
    private String dateTime;

}
