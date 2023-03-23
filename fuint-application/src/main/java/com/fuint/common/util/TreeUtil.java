package com.fuint.common.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fuint.common.domain.TreeNode;
import com.fuint.utils.StringUtil;

/**
 * 树形展示工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TreeUtil {

    /**
     * 创建菜单树状结构
     *
     * @param sourceTreeNodeList 分类node原数据
     * @return 树结构集合
     */
    public static List<TreeNode> sourceTreeNodes(List<TreeNode> sourceTreeNodeList) {
        // 拼装树形结构
        List<TreeNode> nodeList = new ArrayList<>();
        for (TreeNode node1 : sourceTreeNodeList) {
            boolean mark = false;
            for (TreeNode node2 : sourceTreeNodeList) {
                if (StringUtil.isNotEmpty(node1.getName()) && node1.getpId() == node2.getId()) {
                    mark = true;
                    if (node2.getChildrens() == null) {
                        node2.setChildrens(new ArrayList<>());
                    }
                    node2.getChildrens().add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList.stream().sorted(Comparator.comparing(TreeNode::getSort)).collect(Collectors.toList());
    }
}
