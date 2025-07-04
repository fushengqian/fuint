package com.fuint.module.backendApi.request;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="会员ID", name="id")
    private Integer id;

    @ApiModelProperty(value="名称", name="name")
    private String name;

    @ApiModelProperty(value="会员等级", name="gradeId")
    private Integer gradeId;

    @ApiModelProperty(value="分组ID", name="groupId")
    private Integer groupId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="会员号", name="userNo")
    private String userNo;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="性别", name="sex")
    private Integer sex;

    @ApiModelProperty(value="身份证", name="idcard")
    private String idcard;

    @ApiModelProperty(value="生日", name="birthday")
    private String birthday;

    @ApiModelProperty(value="地址", name="address")
    private String address;

    @ApiModelProperty(value="备注信息", name="description")
    private String description;

    @ApiModelProperty(value="状态", name="status")
    private String status;

    @ApiModelProperty(value="会员开始时间", name="startTime")
    private String startTime;

    @ApiModelProperty(value="会员结束时间", name="endTime")
    private String endTime;

}
