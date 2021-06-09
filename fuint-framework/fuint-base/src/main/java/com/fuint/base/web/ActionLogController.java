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
 * 日志管理控制器
 *
 * @author fsq
 * @version $Id: HtActionLogController.java, v 0.1 2015年12月3日 下午2:24:30 fsq Exp $
 */
@Controller
@RequestMapping("/actlog")
public class ActionLogController {


    @Autowired
    private TActionLogService tActionLogService;

    /**
     * 会话列表
     *
     * @param model
     * @return
     */
    @RequiresPermissions("actlog/query")
    @RequestMapping(value = "/query")
    public String actlogList(HttpServletRequest request, HttpServletResponse response, Model model) {
        PaginationResponse<TActionLog> paginationResponse = tActionLogService
                .findLogsByPagination(RequestHandler.buildPaginationRequest(request, model));
        model.addAttribute("paginationResponse", paginationResponse);
        return "actlog/actlog_list";
    }

}
