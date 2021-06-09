package com.fuint.base.web.rest;

import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.entities.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色 RestController
 * <p/>
 * Created by hanxiaoqiang on 16/7/18.
 */
@RestController
@RequestMapping("/duty")
public class DutyRestController {

    @Autowired
    private TDutyService tDutyService;

    @RequestMapping(value = "/tree")
    public List<TreeNode> findSources() {
        List<TreeNode> treeNodes = tDutyService.getDutyTree();
        if (treeNodes != null && treeNodes.size() > 0) {
            treeNodes.get(0).setOpen(true);//默认设置第一个节点为打开状态
        }
        return treeNodes;
    }
}
