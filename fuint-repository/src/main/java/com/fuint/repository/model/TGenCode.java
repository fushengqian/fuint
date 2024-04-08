package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 生成代码实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_gen_code")
@ApiModel(value = "TGenCode对象", description = "生成代码实体")
public class TGenCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("模块名称")
    private String moduleName;

    @ApiModelProperty("表名称")
    private String tableName;

    @ApiModelProperty("表前缀")
    private String tablePrefix;

    @ApiModelProperty("主键名")
    private String pkName;

    @ApiModelProperty("后端包名")
    private String packageName;

    @ApiModelProperty("后端路径")
    private String backendPath;

    @ApiModelProperty("前端路径")
    private String frontPath;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("状态 0 无效 1 有效")
    private String status;

}
