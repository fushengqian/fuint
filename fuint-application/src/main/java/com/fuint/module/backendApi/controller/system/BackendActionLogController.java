package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.ActionLogPage;
import com.fuint.common.service.ActionLogService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.TActionLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台日志管理控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-日志相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/actlog")
public class BackendActionLogController extends BaseController {

    /**
     * 管理员接口
     * */
    private ActionLogService tActionLogService;

    /**
     * 操作日志列表
     */
    @ApiOperation(value = "操作日志列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseObject list(@ModelAttribute ActionLogPage actionLogPage) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            actionLogPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            actionLogPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<TActionLog> paginationResponse = tActionLogService.findLogsByPagination(actionLogPage);
        return getSuccessResult(paginationResponse);
    }
}
