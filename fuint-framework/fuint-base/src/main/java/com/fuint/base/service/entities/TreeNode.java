package com.fuint.base.service.entities;

import java.util.List;

/**
 * 树状结构节点实体
 *
 * Created by hanxiaoqiang on 16/7/14.
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
     * tree 节点是否打开
     */
    private boolean open;

    /**
     * tree 节点是否选中
     */
    private boolean checked;

    /**
     * url
     */
    private String url;

    /**
     * 子菜单
     */
    private List<TreeNode> childrens;

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


    public boolean isOpen() {
        return open;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
