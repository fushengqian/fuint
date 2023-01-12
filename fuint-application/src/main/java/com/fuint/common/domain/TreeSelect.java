package com.fuint.common.domain;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * TreeSelect树结构实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TreeSelect implements Serializable {

    private static final long serialVersionUID = 1L;

    // 节点ID
    private Long id;

    // 节点名称
    private String label;

    // 子节点
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> childrens;

    public TreeSelect() {
       // empty
    }

    public TreeSelect(TreeNode menu) {
        this.id = menu.getId();
        this.label = menu.getName();
        this.childrens = menu.getChildrens().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TreeSelect> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<TreeSelect> childrens) {
        this.childrens = childrens;
    }
}
