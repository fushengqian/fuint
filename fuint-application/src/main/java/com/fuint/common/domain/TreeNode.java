package com.fuint.common.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 树状结构节点实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TreeNode {

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
     * 子菜单
     */
    private List<TreeNode> childrens = new ArrayList<TreeNode>();

    /**
     * 菜单级别
     */
    private int level;

    /**
     * 上级菜单
     */
    private long pId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 新图标
     */
    private String newIcon;

    /**
     * 排序
     * */
    private Integer sort;

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

    public List<TreeNode> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<TreeNode> childrens) {
        this.childrens = childrens;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
