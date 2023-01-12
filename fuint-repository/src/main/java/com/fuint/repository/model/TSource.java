package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_source")
@ApiModel(value = "TSource对象", description = "菜单表")
public class TSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单Id")
    @TableId(value = "source_id", type = IdType.AUTO)
    private Integer sourceId;

    @ApiModelProperty("菜单名称")
    private String sourceName;

    @ApiModelProperty("菜单对应url")
    private String sourceCode;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("字母名称")
    private String ename;

    @ApiModelProperty("新图标")
    private String newIcon;

    @ApiModelProperty("状态(A:可用 D:禁用)")
    private String status;

    @ApiModelProperty("菜单级别")
    private Integer sourceLevel;

    @ApiModelProperty("样式")
    private String sourceStyle;

    @ApiModelProperty("是否显示")
    private Integer isMenu;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("上级菜单ID")
    private Integer parentId;

    private Integer isLog;

    @ApiModelProperty("菜单图标")
    private String icon;


}
