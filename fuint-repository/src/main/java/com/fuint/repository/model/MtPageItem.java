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
 * 页面装修组件明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_page_item")
@ApiModel(value = "MtPageItem对象", description = "页面装修组件明细表")
public class MtPageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("页面ID")
    private Integer pageId;

    @ApiModelProperty("组件类型")
    private String componentType;

    @ApiModelProperty("组件名称")
    private String componentName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("样式JSON")
    private String style;

    @ApiModelProperty("参数JSON")
    private String params;

    @ApiModelProperty("数据JSON")
    private String data;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
