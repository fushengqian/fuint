package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员列表分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberPage extends PageParam implements Serializable {

    @Schema(description="会员ID")
    private Integer id;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="查询关键字")
    private String keyword;

    @Schema(description="查询关键字")
    private String birthday;

    @Schema(description="会员等级ID")
    private Integer gradeId;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="会员开始时间")
    private String startTime;

    @Schema(description="会员结束时间")
    private String endTime;

    @Schema(description="注册时间")
    private String regTime;

    @Schema(description="会员活跃时间")
    private String activeTime;

    @Schema(description="会员有效期")
    private String memberTime;

    @Schema(description="状态")
    private String status;

    @Schema(description="所属店铺ID-多店铺")
    private String storeIds;

    @Schema(description="所属分组ID")
    private String groupIds;

    @Schema(description="会员标签ID-多个用逗号分隔")
    private String tagIds;

}
