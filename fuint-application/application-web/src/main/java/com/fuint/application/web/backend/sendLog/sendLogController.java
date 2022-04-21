package com.fuint.application.web.backend.sendLog;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtSendLog;
import com.fuint.application.service.sendlog.SendLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 发券记录管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/sendLog")
public class sendLogController {

    /**
     * 发送记录服务接口
     */
    @Autowired
    private SendLogService sendLogService;

    /**
     * 发券记录列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/sendLog/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_type = request.getParameter("EQ_type");
        model.addAttribute("EQ_type", EQ_type);

        return "sendLog/index";
    }

    /**
     * 查询发券记录列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/sendLog/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        if (request.getParameter("EQ_type").equals("2")) {
            return "sendLog/list_batch";
        } else {
            return "sendLog/list";
        }
    }
}
