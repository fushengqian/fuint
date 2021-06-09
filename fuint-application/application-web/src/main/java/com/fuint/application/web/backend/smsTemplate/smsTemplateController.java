package com.fuint.application.web.backend.smsTemplate;

import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.service.smstemplate.SmsTemplateService;
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
 * 短信模板管理类controller
 * Created by zach on 2020/04/18.
 */
@Controller
@RequestMapping(value = "/backend/smsTemplate")
public class smsTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(smsTemplateController.class);

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
    @RequiresPermissions("backend/smsTemplate/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_code = request.getParameter("EQ_code");
        model.addAttribute("EQ_code", EQ_code);

        return "smsTemplate/index";
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
    @RequiresPermissions("/backend/smsTemplate/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateService.querySmsTemplateListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        return "smsTemplate/list";
    }

    /**
     * 添加模板初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/smsTemplate/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "smsTemplate/add";
    }

    /**
     * 保存模板页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/smsTemplate/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveHandler(HttpServletRequest request, HttpServletResponse response, Model model, MtSmsTemplateDto smsTemplateDto) throws BusinessCheckException {

        smsTemplateService.saveSmsTemplate(smsTemplateDto);

        return "redirect:/backend/smsTemplate/index";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/smsTemplate/editInit")
    @RequestMapping(value = "/editInit/{id}")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {

        MtSmsTemplate mtSmsTemplate = smsTemplateService.querySmsTemplateById(id.intValue());
        model.addAttribute("smsTemplate", mtSmsTemplate);

        return "smsTemplate/edit";
    }
}
