package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StatisticParam extends PageParam implements Serializable {

    @Schema(description="开始时间", name="startTime")
    private String startTime;

    @Schema(description="结束时间", name="endTime")
    private String endTime;

    @Schema(description="店铺ID", name="storeIds")
    private List<Integer> storeIds;

}
