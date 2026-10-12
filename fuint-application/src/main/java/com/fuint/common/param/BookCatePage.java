package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约分类分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookCatePage extends PageParam implements Serializable {

    @Schema(description="名称")
    private String name;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
