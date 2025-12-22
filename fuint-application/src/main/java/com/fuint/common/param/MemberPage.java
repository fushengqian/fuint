package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("会员ID")
    private Integer id;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("查询关键字")
    private String keyword;

    @ApiModelProperty("查询关键字")
    private String birthday;

    @ApiModelProperty("会员等级ID")
    private Integer gradeId;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("会员开始时间")
    private String startTime;

    @ApiModelProperty("会员结束时间")
    private String endTime;

    @ApiModelProperty("注册时间")
    private String regTime;

    @ApiModelProperty("会员活跃时间")
    private String activeTime;

    @ApiModelProperty("会员有效期")
    private String memberTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("所属店铺ID-多店铺")
    private String storeIds;

    @ApiModelProperty("所属分组ID")
    private String groupIds;

}
