package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单信息实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class SourceDto implements Serializable {

    @ApiModelProperty("自增ID")
    private long id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单名称(字母)")
    private String ename;

    @ApiModelProperty("节点是否打开")
    private Boolean open;

    @ApiModelProperty("是否菜单")
    private int isMenu;

    @ApiModelProperty("节点是否选中")
    private Boolean checked;

    @ApiModelProperty("url")
    private String url;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("权限标识")
    private String perms;

    @ApiModelProperty("菜单级别")
    private int level;

    @ApiModelProperty("上级菜单")
    private long parentId;

    @ApiModelProperty("子菜单")
    private List<SourceDto> children;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("新图标")
    private String newIcon;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("排序")
    private String sort;

    @ApiModelProperty("状态")
    private String status;

}
