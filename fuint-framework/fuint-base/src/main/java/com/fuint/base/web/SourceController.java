package com.fuint.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fuint.base.dao.entities.TSource;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.entities.TreeNode;
import com.fuint.base.service.source.TSourceService;
import com.fuint.base.util.RequestHandler;
import com.fuint.base.util.TreeUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;


/**
 * 菜单管理控制类
 *
 * @author fsq
 * @version $Id: HtSourceController.java, v 0.1 2015年11月19日 下午4:58:08 fsq Exp $
 */
@Controller
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private TSourceService sSourceService;

    /**
     * 菜单信息列表展现
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息列表展现页面
     */
    @RequiresPermissions("source/query")
    @RequestMapping(value = "/query")
    public String sourceList(HttpServletRequest request, HttpServletResponse response,
                             Model model) {
        PaginationResponse<TSource> paginationResponse = sSourceService
                .findPlatformByPagination(RequestHandler.buildPaginationRequest(request, model));
        model.addAttribute("paginationResponse", paginationResponse);

        List<TreeNode> sources = sSourceService.getSourceTree();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeUtil.convert(TreeUtil.sourceTreeNodes(sources), treeNodes);
        model.addAttribute("sourceTree", treeNodes);
        return "source/source_list";
    }

    /**
     * 新增菜单页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息新增页面
     */
    @RequiresPermissions("source/add")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addSource(HttpServletRequest request, HttpServletResponse response,
                            Model model) {
        List<TreeNode> sources = sSourceService.getSourceTree();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeUtil.convert(TreeUtil.sourceTreeNodes(sources), treeNodes);
        model.addAttribute("sourceTree", treeNodes);
        return "source/source_add";
    }

    /**
     * 新增菜单页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("source/add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSourceHandler(HttpServletRequest request, HttpServletResponse response,
                                   Model model) throws BusinessCheckException {
        String parentCode = request.getParameter("parentCode");//父菜单编码
        TSource addSource = (TSource) RequestHandler.createBean(request,
                new TSource());
        if (StringUtil.isNotBlank(parentCode)) {
            TSource parentSource = null;
            try {
                parentSource = sSourceService.findSourceById(Long.parseLong(parentCode));
                addSource.setParent(parentSource);
                addSource.setLevel(parentSource.getLevel() + 1);
            } catch (Exception e) {
                throw new BusinessCheckException("父菜单" + parentCode + "不存在");
            }
        } else {
            addSource.setLevel(1);
        }
        sSourceService.addSource(addSource);
        return "redirect:/source/query";
    }

    /**
     * 修改菜单页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息修改页面
     */
    @RequiresPermissions("source/edit")
    @RequestMapping(value = "/edit/{sourceid}", method = RequestMethod.GET)
    public String editSource(HttpServletRequest request, HttpServletResponse response, Model model,
                             @PathVariable("sourceid") Long sourceid) {
        TSource tSource = this.sSourceService.findSourceById(sourceid);
        List<TreeNode> sources = sSourceService.getSourceTree();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeUtil.convert(TreeUtil.sourceTreeNodes(sources), treeNodes);
        model.addAttribute("sourceTree", treeNodes);
        model.addAttribute("tSource", tSource);
        return "source/source_edit";
    }

    /**
     * 修改菜单处理
     *
     * @param request
     * @param response
     * @param model
     * @param sourceid
     * @return
     */
    @RequiresPermissions("source/edit")
    @RequestMapping(value = "/edit/{sourceid}", method = RequestMethod.POST)
    public String editSourceHandler(HttpServletRequest request, HttpServletResponse response, Model model,
                                    @PathVariable("sourceid") Long sourceid) throws BusinessCheckException {
        TSource editSource = (TSource) RequestHandler.createBean(request,
                new TSource());
        editSource.setId(sourceid);
        String parentCode = request.getParameter("parentCode");//父菜单编码
        if (StringUtil.isNotBlank(parentCode)) {
            TSource parentSource = null;
            try {
                parentSource = sSourceService.findSourceById(Long.parseLong(parentCode));
                editSource.setParent(parentSource);
                editSource.setLevel(parentSource.getLevel() + 1);
            } catch (Exception e) {
                throw new BusinessCheckException("父菜单" + parentCode + "不存在");
            }
        } else {
            editSource.setLevel(1);
        }
        sSourceService.editSource(editSource);
        return "redirect:/source/query";
    }

    /**
     * 删除菜单信息
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 跳转回账户列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("source/delete")
    @RequestMapping(value = "/delete/{sourceid}", method = RequestMethod.GET)
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response,
                                Model model,
                                @PathVariable("sourceid") Long sourceid) throws BusinessCheckException {
        try {
            sSourceService.deleteSource(sourceid);
        }catch(Exception e){
            throw new BusinessCheckException("存在子菜单,不能删除.");
        }
        return "redirect:/source/query";
    }

}
