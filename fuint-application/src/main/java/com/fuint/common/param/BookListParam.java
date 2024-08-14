package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="名称", name="name")
    private String name;

    @ApiModelProperty(value="分类ID", name="cateId")
    private Integer cateId;

    @ApiModelProperty(value="商户号", name="merchantNo")
    private String merchantNo;

}
