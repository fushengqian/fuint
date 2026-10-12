package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberListParam extends PageParam implements Serializable {

    @Schema(description="ID", name="id")
    private String id;

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="会员ID", name="userId")
    private Integer userId;

    @Schema(description="关键字", name="keyword")
    private String keyword;

    @Schema(description="会员名称", name="name")
    private String name;

    @Schema(description="会员生日", name="birthday")
    private String birthday;

    @Schema(description="会员号", name="userNo")
    private String userNo;

    @Schema(description="会员等级", name="gradeId")
    private String gradeId;

    @Schema(description="注册时间", name="regTime")
    private String regTime;

    @Schema(description="活跃时间", name="activeTime")
    private String activeTime;

    @Schema(description="会员有效期", name="memberTime")
    private String memberTime;

    @Schema(description="数据类型，1）todayRegister：今日注册；2）todayActive：今日活跃", name="dataType")
    private String dataType;

    @Schema(description="会员状态", name="status")
    private String status;

}
