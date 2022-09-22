package com.fuint.base.web;

import com.fuint.base.dao.entities.TActionLog;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.log.TActionLogService;
import com.fuint.base.util.RequestHandler;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志查询
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/log")
public class LogController {

    @Autowired
    private TActionLogService tActionLogService;

    /**
     * 日志信息列表展现
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息列表展现页面
     */
    @RequiresPermissions("log/query")
    @RequestMapping(value = "/query")
    public String accountList(HttpServletRequest request, HttpServletResponse response, Model model) {
        PaginationResponse<TActionLog> paginationResponse = tActionLogService.findLogsByPagination(RequestHandler.buildPaginationRequest(request, model));
        model.addAttribute("paginationResponse", paginationResponse);
        return "log/log_list";
    }
}
