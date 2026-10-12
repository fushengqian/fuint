package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 员工请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffParam implements Serializable {

    @Schema(description="ID", name="id")
    private Integer id;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="类别", name="category")
    private Integer category;

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="真实姓名", name="realName")
    private String realName;

    @Schema(description="头像", name="avatar")
    private String avatar;

    @Schema(description="备注信息", name="description")
    private String description;

    @Schema(description="审核状态", name="auditedStatus")
    private String auditedStatus;

}
