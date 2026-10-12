package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 已发短信分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SmsPage extends PageParam implements Serializable {

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="内容")
    private String content;

    @Schema(description="状态")
    private String status;

}
