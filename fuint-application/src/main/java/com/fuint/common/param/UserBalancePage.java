package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员余额请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserBalancePage extends PageParam implements Serializable {

    @Schema(description="状态，A正常；D作废")
    private String status;

}
