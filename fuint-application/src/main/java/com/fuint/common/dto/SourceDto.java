package com.fuint.common.dto;

/**
 * 菜单信息实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class SourceDto {

    /**
     * 菜单ID
     */
    private long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单名称(字母)
     */
    private String ename;

    /**
     * tree 节点是否打开
     */
    private boolean open;

    /**
     * 是否菜单
     * */
    private int isMenu;

    /**
     * tree 节点是否选中
     */
    private boolean checked;

    /**
     * url
     */
    private String url;

    /**
     * path
     */
    private String path;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单级别
     */
    private int level;

    /**
     * 上级菜单
     */
    private long parentId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 新图标
     */
    private String newIcon;

    /**
     * 描述信息
     * */
    private String description;

    /**
     * 排序
     * */
    private String sort;

    /**
     * 状态
     * */
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public boolean isOpen() {
        return open;
    }

    public int getIsMenu() {
        return isMenu;
    }

    public void setIsMenu(int isMenu) {
        this.isMenu = isMenu;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNewIcon() {
        return newIcon;
    }

    public void setNewIcon(String newIcon) {
        this.newIcon = newIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
