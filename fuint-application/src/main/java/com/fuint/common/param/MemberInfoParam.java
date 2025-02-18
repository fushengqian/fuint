package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员查询请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberInfoParam extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="头像", name="avatar")
    private String avatar;

    @ApiModelProperty(value="称呼", name="name")
    private String name;

    @ApiModelProperty(value="性别", name="sex")
    private Integer sex;

    @ApiModelProperty(value="生日", name="birthday")
    private String birthday;

    @ApiModelProperty(value="会员号", name="userNo")
    private String userNo;

}
