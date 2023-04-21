package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.service.ActionLogService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.TActionLog;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志管理控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-日志相关接口")
@RestController
@RequestMapping(value = "/backendApi/actlog")
public class BackendActionLogController extends BaseController {

    @Autowired
    private ActionLogService tActionLogService;

    /**
     * 日志列表
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ResponseObject list(HttpServletRequest request) {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String accountName = request.getParameter("accountName") == null ? "" : request.getParameter("accountName");
        String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
        String beginTime = request.getParameter("params[beginTime]") == null ? "" : request.getParameter("params[beginTime]");
        String endTime = request.getParameter("params[endTime]") == null ? "" : request.getParameter("params[endTime]");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(accountName)) {
            searchParams.put("name", accountName);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            searchParams.put("module", keyword);
        }
        if (StringUtil.isNotEmpty(beginTime)) {
            searchParams.put("startTime", beginTime + " 00:00:00");
        }
        if (StringUtil.isNotEmpty(endTime)) {
            searchParams.put("endTime", endTime + " 23:59:59");
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"actionTime desc"});

        PaginationResponse<TActionLog> paginationResponse = tActionLogService.findLogsByPagination(paginationRequest);
        return getSuccessResult(paginationResponse);
    }
}
