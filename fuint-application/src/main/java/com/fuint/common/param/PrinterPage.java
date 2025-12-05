package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 打印机分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PrinterPage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("打印机编号")
    private String sn;

    @ApiModelProperty("打印机名称")
    private String name;

    @ApiModelProperty("是否自动打印机")
    private String autoPrint;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
