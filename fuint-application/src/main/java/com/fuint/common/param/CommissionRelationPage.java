package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 分销提成邀请记录分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRelationPage extends PageParam implements Serializable {

    @Schema(description="商户号")
    private String merchantNo;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="姓名")
    private String realName;

    @Schema(description="类型")
    private String subUserId;

    @Schema(description="开始时间")
    private String startTime;

    @Schema(description="结束时间")
    private String endTime;

    @Schema(description="状态，A正常；D作废")
    private String status;

    @Schema(description="等级，1：一级；2：二级")
    private Integer level;

}
