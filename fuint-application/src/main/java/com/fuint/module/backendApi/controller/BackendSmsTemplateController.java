package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.SmsTemplateDto;
import com.fuint.common.service.SmsTemplateService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtSmsTemplate;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信模板管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-短信模板相关接口")
@RestController
@RequestMapping(value = "/backendApi/smsTemplate")
public class BackendSmsTemplateController extends BaseController {

    /**
     * 短信模板服务接口
     */
    @Autowired
    private SmsTemplateService smsTemplateService;

    /**
     * 查询短信模板列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("content") == null ? "" : request.getParameter("content");
        String code = request.getParameter("code") == null ? "" : request.getParameter("code");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(code)) {
            searchParams.put("code", code);
        }
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("name", name);
        }
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateService.querySmsTemplateListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 保存模板页面
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody SmsTemplateDto smsTemplateDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        smsTemplateService.saveSmsTemplate(smsTemplateDto);
        return getSuccessResult(true);
    }

    /**
     * 模板详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtSmsTemplate mtSmsTemplate = smsTemplateService.querySmsTemplateById(id.intValue());

        Map<String, Object> result = new HashMap();
        result.put("smsTemplate", mtSmsTemplate);

        return getSuccessResult(result);
    }

    /**
     * 删除数据项
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        smsTemplateService.deleteTemplate(id, operator);

        return getSuccessResult(true);
    }
}
