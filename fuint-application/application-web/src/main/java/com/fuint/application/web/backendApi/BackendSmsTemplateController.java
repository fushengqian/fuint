package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.service.smstemplate.SmsTemplateService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信模板管理类controller
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/smsTemplate")
public class BackendSmsTemplateController extends BaseController {

    /**
     * 短信模板服务接口
     */
    @Autowired
    private SmsTemplateService smsTemplateService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

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
        String content = request.getParameter("content") == null ? "" : request.getParameter("content");
        String code = request.getParameter("code") == null ? "" : request.getParameter("code");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(code)) {
            searchParams.put("EQ_code", code);
        }
        if (StringUtil.isNotEmpty(content)) {
            searchParams.put("LIKE_content", content);
        }
        searchParams.put("NQ_status", StatusEnum.DISABLE.getKey());
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
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody MtSmsTemplateDto smsTemplateDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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
    @RequestMapping(value = "/info/{id}")
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        smsTemplateService.deleteTemplate(id, operator);

        return getSuccessResult(true);
    }
}
