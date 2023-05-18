package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 文章列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ArticleListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="文章标题", name="title")
    private String title;

}
