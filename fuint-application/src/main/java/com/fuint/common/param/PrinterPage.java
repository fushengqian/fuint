package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="打印机编号")
    private String sn;

    @Schema(description="打印机名称")
    private String name;

    @Schema(description="是否自动打印机")
    private String autoPrint;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
