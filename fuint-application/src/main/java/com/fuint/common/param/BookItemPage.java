package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约订单分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookItemPage extends PageParam implements Serializable {

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="联系人")
    private String contact;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
