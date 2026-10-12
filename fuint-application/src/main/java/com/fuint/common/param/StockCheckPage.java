package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 库存盘点分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StockCheckPage extends PageParam implements Serializable {

    @Schema(description="盘点单号")
    private String checkNo;

    @Schema(description="状态，A：盘点中；B：已完成；D：已作废")
    private String status;

    @Schema(description="盘点开始时间")
    private String checkTimeBegin;

    @Schema(description="盘点结束时间")
    private String checkTimeEnd;

    @Schema(description="搜索关键词")
    private String description;
}
