package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 页面装修组件明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_page_item")
@Schema(name="MtPageItem对象", description="页面装修组件明细表")
public class MtPageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="页面ID")
    private Integer pageId;

    @Schema(description="组件类型")
    private String componentType;

    @Schema(description="组件名称")
    private String componentName;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="样式JSON")
    private String style;

    @Schema(description="参数JSON")
    private String params;

    @Schema(description="数据JSON")
    private String data;

    @Schema(description="状态")
    private String status;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;
}
