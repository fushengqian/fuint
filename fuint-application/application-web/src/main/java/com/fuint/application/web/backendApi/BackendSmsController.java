package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信管理类controller
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/smsManager")
public class BackendSmsController extends BaseController {

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询已发短信列表
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
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String content = request.getParameter("content") == null ? "" : request.getParameter("content");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(mobile)) {
            searchParams.put("EQ_mobilePhone", mobile);
        }
        if (StringUtil.isNotEmpty(content)) {
            searchParams.put("LIKE_content", content);
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSmsSendedLog> paginationResponse = sendSmsService.querySmsListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }
}
