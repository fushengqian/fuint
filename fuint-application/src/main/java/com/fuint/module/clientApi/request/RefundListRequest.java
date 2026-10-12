package com.fuint.module.clientApi.request;

import com.fuint.common.param.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 售后列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundListRequest extends PageParam implements Serializable {

    @Schema(description="会员ID", name="userId")
    private Integer userId;

    @Schema(description="关键字", name="keyword")
    private String keyword;

    @Schema(description="状态", name="status")
    private String status;

}
