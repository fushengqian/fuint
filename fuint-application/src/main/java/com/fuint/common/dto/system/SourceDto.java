package com.fuint.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单信息实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SourceDto implements Serializable {

    @Schema(description="自增ID")
    private long id;

    @Schema(description="商户ID")
    private Integer merchantId;

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

    @Schema(description="菜单级别")
    private int level;

    @Schema(description="上级菜单")
    private long parentId;

    @Schema(description="子菜单")
    private List<SourceDto> children;

    @Schema(description="图标")
    private String icon;

    @Schema(description="新图标")
    private String newIcon;

    @Schema(description="描述信息")
    private String description;

    @Schema(description="排序")
    private String sort;

    @Schema(description="状态")
    private String status;

}
