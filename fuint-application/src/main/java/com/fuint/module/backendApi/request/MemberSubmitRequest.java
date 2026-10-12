package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 提交会员信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberSubmitRequest implements Serializable {

    @Schema(description="会员ID", name="id")
    private Integer id;

    @Schema(description="名称", name="name")
    private String name;

    @Schema(description="会员等级", name="gradeId")
    private Integer gradeId;

    @Schema(description="分组ID", name="groupId")
    private Integer groupId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="会员号", name="userNo")
    private String userNo;

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="性别", name="sex")
    private Integer sex;

    @Schema(description="身份证", name="idcard")
    private String idcard;

    @Schema(description="生日", name="birthday")
    private String birthday;

    @Schema(description="地址", name="address")
    private String address;

    @Schema(description="备注信息", name="description")
    private String description;

    @Schema(description="状态", name="status")
    private String status;

    @Schema(description="会员开始时间", name="startTime")
    private String startTime;

    @Schema(description="会员结束时间", name="endTime")
    private String endTime;

    @Schema(description="标签ID列表，多个用逗号分隔", name="tagIds")
    private String tagIds;

}
