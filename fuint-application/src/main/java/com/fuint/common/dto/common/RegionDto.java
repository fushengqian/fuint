package com.fuint.common.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 地区实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RegionDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="名称")
    private String name;

    @Schema(description="父ID")
    private Integer pid;

    @Schema(description="编码")
    private String code;

    @Schema(description="层级")
    private String level;

    @Schema(description="城市")
    private List<RegionDto> city;

    @Schema(description="区域")
    private List<RegionDto> region;

}
