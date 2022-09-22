package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.base.dao.entities.TActionLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.log.TActionLogService;
import com.fuint.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 日志管理控制器
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/actlog")
public class BackendActionLogController extends BaseController {

    @Autowired
    private TActionLogService tActionLogService;

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

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(accountName)) {
            searchParams.put("EQ_acctName", accountName);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            searchParams.put("EQ_accountStatus", keyword);
        }
        if (StringUtil.isNotEmpty(beginTime)) {
            searchParams.put("GT_actionTime", beginTime + " 00:00:00");
        }
        if (StringUtil.isNotEmpty(endTime)) {
            searchParams.put("LT_actionTime", endTime + " 23:59:59");
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"actionTime desc"});

        PaginationResponse<TActionLog> paginationResponse = tActionLogService.findLogsByPagination(paginationRequest);
        return getSuccessResult(paginationResponse);
    }
}
