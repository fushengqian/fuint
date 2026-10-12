package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 装修页面表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_page")
@Schema(name="MtPage对象", description="装修页面表")
public class MtPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID，0表示平台")
    private Integer merchantId;

    @Schema(description="店铺ID，0表示全部店铺")
    private Integer storeId;

    @Schema(description="页面名称")
    private String pageName;

    @Schema(description="页面类型：index首页、custom自定义页面")
    private String pageType;

    @Schema(description="是否默认：Y是 N否")
    private String isDefault;

    @Schema(description="分享标题")
    private String shareTitle;

    @Schema(description="分享logo")
    private String shareLogo;

    @Schema(description="状态：A启用 N禁用 D删除")
    private String status;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="操作人")
    private String operator;
}
