package com.fuint.base.web.rest;

import com.fuint.base.service.entities.TreeNode;
import com.fuint.base.service.source.TSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单RestController
 *
 * Created by hanxiaoqiang on 16/7/15.
 */
@RestController
@RequestMapping("/source")
public class SourceRestController {

    @Autowired
    private TSourceService sSourceService;

    @RequestMapping(value = "/tree")
    public List<TreeNode> findSources(){
        List<TreeNode> sourceTreeNodes = sSourceService.getSourceTree();
        if(sourceTreeNodes != null && sourceTreeNodes.size() > 0){
            sourceTreeNodes.get(0).setOpen(true);//默认设置第一个节点为打开状态
        }
        return sourceTreeNodes;
    }
}
