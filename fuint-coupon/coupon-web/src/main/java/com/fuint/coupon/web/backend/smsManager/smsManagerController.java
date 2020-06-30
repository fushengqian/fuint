package com.fuint.coupon.web.backend.smsManager;

import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.*;
import com.fuint.coupon.service.smstemplate.SmsTemplateService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信管理类controller
 * Created by zach on 2020/04/26.
 */
@Controller
@RequestMapping(value = "/backend/smsManager")
public class smsManagerController {

    private static final Logger logger = LoggerFactory.getLogger(smsManagerController.class);

    /**
     * 短信模板服务接口
     */
    @Autowired
    private SmsTemplateService smsTemplateService;

    /**
     * 优惠分组列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/smsManager/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_code = request.getParameter("EQ_code");
        model.addAttribute("EQ_code", EQ_code);

        return "smsManager/index";
    }

    /**
     * 查询优惠分组列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/smsManager/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateService.querySmsTemplateListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        return "smsManager/list";
    }
}
