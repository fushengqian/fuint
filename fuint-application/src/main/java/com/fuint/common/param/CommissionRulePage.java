package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分销提成规则分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRulePage extends PageParam implements Serializable {

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="类型")
    private String type;

    @Schema(description="规则名称")
    private String name;

    @Schema(description="分佣对象")
    private String target;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
