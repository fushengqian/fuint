package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("盘点单号")
    private String checkNo;

    @ApiModelProperty("状态，A：盘点中；B：已完成；D：已作废")
    private String status;

    @ApiModelProperty("盘点开始时间")
    private String checkTimeBegin;

    @ApiModelProperty("盘点结束时间")
    private String checkTimeEnd;

    @ApiModelProperty("搜索关键词")
    private String description;
}
