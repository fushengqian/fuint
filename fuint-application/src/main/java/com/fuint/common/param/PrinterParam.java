package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PrinterParam implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
