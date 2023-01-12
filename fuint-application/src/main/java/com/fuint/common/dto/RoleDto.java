package com.fuint.common.dto;

/**
 * 角色信息实体类
 */
public class RoleDto {

    /**
     * 账户主键id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     * */
    private String type;

    /**
     * 备注
     * */
    private String description;

    /**
     * 状态 : A 有效 D 无效
     */
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
