package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 操作日志请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ActionLogPage extends PageParam implements Serializable {

    @Schema(description="账号名称")
    private String accountName;

    @Schema(description="ip地址")
    private String ip;

    @Schema(description="模块")
    private String module;

    @Schema(description="搜索关键字")
    private String keyword;

    @Schema(description="开始时间")
    private String beginTime;

    @Schema(description="结束时间")
    private String endTime;

}
