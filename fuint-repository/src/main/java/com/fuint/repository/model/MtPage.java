package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 装修页面表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_page")
@ApiModel(value = "MtPage对象", description = "装修页面表")
public class MtPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商户ID，0表示平台")
    private Integer merchantId;

    @ApiModelProperty("店铺ID，0表示全部店铺")
    private Integer storeId;

    @ApiModelProperty("页面名称")
    private String pageName;

    @ApiModelProperty("页面类型：index首页、custom自定义页面")
    private String pageType;

    @ApiModelProperty("是否默认：Y是 N否")
    private String isDefault;

    @ApiModelProperty("分享标题")
    private String shareTitle;

    @ApiModelProperty("分享logo")
    private String shareLogo;

    @ApiModelProperty("状态：A启用 N禁用 D删除")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("操作人")
    private String operator;
}
