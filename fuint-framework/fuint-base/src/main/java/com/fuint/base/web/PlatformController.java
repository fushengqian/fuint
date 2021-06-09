package com.fuint.base.web;

import com.fuint.base.dao.entities.TPlatform;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.platform.TPlatformService;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 平台管理
 * <p/>
 * Created by hanxiaoqiang on 16/8/1.
 */
@Controller
@RequestMapping("/platform")
public class PlatformController {

    private static final Logger logger = LoggerFactory.getLogger(PlatformController.class);

    @Autowired
    private TPlatformService tPlatformService;

    /**
     * 平台列表查询
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("platform/query")
    @RequestMapping(value = "/query")
    public String platformList(HttpServletRequest request, HttpServletResponse response,
                               Model model) {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_status", "1");
        paginationRequest.setSearchParams(params);
        PaginationResponse<TPlatform> paginationResponse = tPlatformService
                .findPlatformByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);
        return "platform/platform_list";
    }

    /**
     * 新增平台
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("platform/add")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addPlatform(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "platform/platform_add";
    }

    /**
     * 新增平台页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 平台列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("platform/add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPlatformHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model) throws BusinessCheckException {
        tPlatformService.addPlatform((TPlatform) RequestHandler.createBean(request,
                new TPlatform()));
        return "redirect:/platform/query";
    }

    /**
     * 删除平台
     *
     * @param request
     * @param response
     * @param model
     * @param platformId
     * @return
     */
    @RequiresPermissions("platform/delete")
    @RequestMapping(value = "/delete/{platformId}", method = RequestMethod.GET)
    public String deletePlatform(HttpServletRequest request, HttpServletResponse response,
                                 Model model, @PathVariable("platformId") Long platformId) throws BusinessCheckException {
        tPlatformService.deletePlatform(platformId);
        return "redirect:/platform/query";
    }
}
