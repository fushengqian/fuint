package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 文章详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ArticleDetailParam implements Serializable {

    @Schema(description="文章ID", name="articleId")
    private Integer articleId;

}
