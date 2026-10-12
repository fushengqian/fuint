package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 生成代码实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_gen_code")
@Schema(name="TGenCode对象", description="生成代码实体")
public class TGenCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="服务名称")
    private String serviceName;

    @Schema(description="模块名称")
    private String moduleName;

    @Schema(description="表名称")
    private String tableName;

    @Schema(description="表前缀")
    private String tablePrefix;

    @Schema(description="主键名")
    private String pkName;

    @Schema(description="后端包名")
    private String packageName;

    @Schema(description="后端路径")
    private String backendPath;

    @Schema(description="前端路径")
    private String frontPath;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="作者")
    private String author;

    @Schema(description="状态 0 无效 1 有效")
    private String status;

}
