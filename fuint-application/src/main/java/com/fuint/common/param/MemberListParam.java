package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private String id;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="会员名称", name="name")
    private String name;

    @ApiModelProperty(value="会员生日", name="birthday")
    private String birthday;

    @ApiModelProperty(value="会员号", name="userNo")
    private String userNo;

    @ApiModelProperty(value="会员等级", name="gradeId")
    private String gradeId;

    @ApiModelProperty(value="注册时间", name="regTime")
    private String regTime;

    @ApiModelProperty(value="活跃时间", name="activeTime")
    private String activeTime;

    @ApiModelProperty(value="会员有效期", name="memberTime")
    private String memberTime;

    @ApiModelProperty(value="数据类型，1）todayRegister：今日注册；2）todayActive：今日活跃", name="dataType")
    private String dataType;

    @ApiModelProperty(value="会员状态", name="status")
    private String status;

}
