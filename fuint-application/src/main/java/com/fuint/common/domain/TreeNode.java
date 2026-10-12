package com.fuint.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树状结构节点实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class TreeNode implements Serializable {

    @Schema(description="菜单ID")
    private long id;

    @Schema(description="菜单名称")
    private String name;

    @Schema(description="菜单名称(字母)")
    private String ename;

    @Schema(description="节点是否打开")
    private Boolean open;

    @Schema(description="是否菜单")
    private int isMenu;

    @Schema(description="节点是否选中")
    private Boolean checked;

    @Schema(description="url")
    private String url;

    @Schema(description="路径")
    private String path;

    @Schema(description="权限标识")
    private String perms;

    @Schema(description="子菜单")
    private List<TreeNode> childrens = new ArrayList<>();

    @Schema(description="菜单级别")
    private int level;

    @Schema(description="上级菜单")
    private long pId;

    @Schema(description="图标")
    private String icon;

    @Schema(description="新图标")
    private String newIcon;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="状态")
    private String status;

}
