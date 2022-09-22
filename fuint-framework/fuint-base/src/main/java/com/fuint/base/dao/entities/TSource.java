package com.fuint.base.dao.entities;

import java.util.Set;
import javax.persistence.*;

/**
 * 菜单实体类
 *
 * @author FSQ
 * @version $Id: TSource.java
 */
@Entity
@Table(name = "t_source")
@NamedQuery(name = "TSource.findAll", query = "SELECT c FROM TSource c")
public class TSource implements java.io.Serializable {

    /**
     * UUID
     */
    private static final long serialVersionUID = -5175246456181078279L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "source_id")
    private Long id;

    /**
     * 菜单名称
     */
    @Column(name = "source_name")
    private String name;

    /**
     * 菜单名称(字母)
     */
    @Column(name = "ename")
    private String ename;

    /**
     * 菜单地址
     */
    @Column(name = "source_code")
    private String sourceCode;

    /**
     * 菜单路径
     */
    @Column(name = "path")
    private String path;

    /**
     * 菜单级别
     */
    @Column(name = "source_level")
    private Integer level;
    /**
     * 菜单排序
     */
    @Column(name = "source_style")
    private String style;
    /**
     * 关联权限方法
     */
    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST},mappedBy = "tSource",fetch = FetchType.LAZY)
    private Set<TDutySource> tDutySources;

    /**
     * 是否为菜单
     */
    @Column(name = "is_menu")
    private int isMenu;

    /**
     * 是否记录日志
     */
    @Column(name = "is_log")
    private int isLog;
    /**
     * 状态
     */
    @Column
    private String status;
    /**
     * 父级菜单ID
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TSource parent;

    /**
     * 描述
     */
    @Column
    private String description;

    /**
     * 图标
     */
    @Column
    private String icon;

    /**
     * 新图标
     */
    @Column(name = "new_icon")
    private String newIcon;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIsMenu() {
        return isMenu;
    }

    public void setIsMenu(int isMenu) {
        this.isMenu = isMenu;
    }

    /**
     * default constructor
     */
    public TSource() {
    }

    public String getSourceCode() {
        return this.sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public TSource getParent() {
        return parent;
    }

    public void setParent(TSource parent) {
        this.parent = parent;
    }

    public Set<TDutySource> gettDutySources() {
        return tDutySources;
    }

    public void settDutySources(Set<TDutySource> tDutySources) {
        this.tDutySources = tDutySources;
    }

    public int getIsLog() {
        return isLog;
    }

    public void setIsLog(int isLog) {
        this.isLog = isLog;
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
}
