package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookListParam extends PageParam implements Serializable {

    @Schema(description="名称", name="name")
    private String name;

    @Schema(description="分类ID", name="cateId")
    private Integer cateId;

    @Schema(description="商户号", name="merchantNo")
    private String merchantNo;

}
