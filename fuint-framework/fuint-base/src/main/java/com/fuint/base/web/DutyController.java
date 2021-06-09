package com.fuint.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fuint.base.dao.entities.TDuty;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.source.TSourceService;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/**
 * 角色管理控制类
 *
 * @author fsq
 * @version $Id: RoleController.java, v 0.1 2015年11月16日 下午3:08:02 fsq Exp $
 */
@Controller
@RequestMapping("/duty")
public class DutyController {

    @Autowired
    private TDutyService tDutyService;

    @Autowired
    private TSourceService tSourceService;

    /**
     * 账户角色列表展现
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 角色信息列表展现页面
     */
    @RequiresPermissions("duty/query")
    @RequestMapping(value = "/query")
    public String dutyList(HttpServletRequest request, HttpServletResponse response,
                           Model model) {
        PaginationResponse<TDuty> paginationResponse = tDutyService
                .findDutiesByPagination(RequestHandler.buildPaginationRequest(request, model));
        model.addAttribute("paginationResponse", paginationResponse);
        return "duty/duty_list";
    }

    /**
     * 新增角色页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 角色信息新增页面
     */
    @RequiresPermissions("duty/add")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addDuty(HttpServletRequest request, HttpServletResponse response,
                          Model model) {
        return "duty/duty_add";
    }

    /**
     * 新增角色页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 角色列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("duty/add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addDutyHandler(HttpServletRequest request, HttpServletResponse response,
                                 Model model) throws BusinessCheckException {
        String params = request.getParameter("params");//获取角色所分配的菜单
        List<TSource> sources = null;
        if (StringUtil.isNotBlank(params)) {
            String[] sourceIds = params.split(",");
            sources = tSourceService.findDatasByIds(sourceIds);
            if (sources.size() < sourceIds.length) {
                throw new BusinessCheckException("分配的菜单不存在.");
            }
        }
        //添加角色信息
        tDutyService.saveDuty((TDuty) RequestHandler.createBean(request,
                new TDuty()), sources);
        return "redirect:/duty/query";
    }

    /**
     * 修改角色页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息修改页面
     */
    @RequiresPermissions("duty/edit")
    @RequestMapping(value = "/edit/{dutyid}", method = RequestMethod.GET)
    public String editDuty(HttpServletRequest request, HttpServletResponse response, Model model,
                           @PathVariable("dutyid") Long dutyid) {
        TDuty htDuty = tDutyService.getRoleById(dutyid);
        model.addAttribute("duty", htDuty);
        List<Long> sources = tDutyService.getSourceIdsByDutyId(dutyid);
        if (sources != null && sources.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < sources.size(); i++) {
                stringBuffer.append(sources.get(i));
                if (i + 1 < sources.size()) {
                    stringBuffer.append(",");
                }
            }
            model.addAttribute("selectedSources", stringBuffer.toString());
        }
        return "duty/duty_edit";
    }

    /**
     * 修改角色处理
     *
     * @param request
     * @param response
     * @param model
     * @param dutyid
     * @return
     */
    @RequiresPermissions("duty/edit")
    @RequestMapping(value = "/edit/{dutyid}", method = RequestMethod.POST)
    public String editDutyHandler(HttpServletRequest request, HttpServletResponse response, Model model,
                                  @PathVariable("dutyid") Long dutyid) throws BusinessCheckException {
        TDuty duty = (TDuty) RequestHandler.createBean(request,
                new TDuty());
        duty.setId(dutyid);
        String params = request.getParameter("params");//获取角色所分配的菜单
        List<TSource> sources = null;
        if (StringUtil.isNotBlank(params)) {
            String[] sourceIds = params.split(",");
            sources = tSourceService.findDatasByIds(sourceIds);
            if (sources.size() < sourceIds.length) {
                throw new BusinessCheckException("分配的菜单不存在.");
            }
        }
        tDutyService.updateDuty(duty, sources);
        return "redirect:/duty/query";
    }

    /**
     * 删除角色信息
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 跳转回角色列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("duty/delete")
    @RequestMapping(value = "/delete/{dutyid}", method = RequestMethod.GET)
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response,
                                Model model,
                                @PathVariable("dutyid") Long dutyid) throws BusinessCheckException {
        tDutyService.deleteDuty(dutyid);
        return "redirect:/duty/query";
    }

}
