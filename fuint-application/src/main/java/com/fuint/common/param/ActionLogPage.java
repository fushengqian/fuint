package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("搜索关键字")
    private String keyword;

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;

}
