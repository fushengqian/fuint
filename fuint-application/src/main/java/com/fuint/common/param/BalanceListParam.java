package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 余额明细列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BalanceListParam extends PageParam implements Serializable {

    @Schema(description="会员ID", name="userId")
    private String userId;

}
