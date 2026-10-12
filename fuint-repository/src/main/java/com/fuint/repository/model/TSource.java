package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_source")
@Schema(name="TSource对象", description="菜单表")
public class TSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="菜单Id")
    @TableId(value = "source_id", type = IdType.AUTO)
    private Integer sourceId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="菜单名称")
    private String sourceName;

    @Schema(description="菜单对应url")
    private String sourceCode;

    @Schema(description="路径")
    private String path;

    @Schema(description="字母名称")
    private String ename;

    @Schema(description="新图标")
    private String newIcon;

    @Schema(description="状态(A:可用 D:禁用)")
    private String status;

    @Schema(description="菜单级别")
    private Integer sourceLevel;

    @Schema(description="样式")
    private String sourceStyle;

    @Schema(description="是否显示")
    private Integer isMenu;

    @Schema(description="描述")
    private String description;

    @Schema(description="上级菜单ID")
    private Integer parentId;

    private Integer isLog;

    @Schema(description="菜单图标")
    private String icon;

}
