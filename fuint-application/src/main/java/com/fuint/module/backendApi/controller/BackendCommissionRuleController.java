package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.CommissionRuleService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtCommissionRule;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销提成规则管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-分销提成规则相关接口")
@RestController
@RequestMapping(value = "/backendApi/commissionRule")
public class BackendCommissionRuleController extends BaseController {

    /**
     * 分销提成规则服务接口
     */
    @Autowired
    private CommissionRuleService commissionRuleService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 规则列表查询
     *
     * @param  request  HttpServletRequest对象
     * @return 规则列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId;
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        } else {
            storeId = accountInfo.getStoreId();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(title)) {
            params.put("title", title);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtCommissionRule> paginationResponse = commissionRuleService.queryDataByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新分销提成规则状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtCommissionRule mtCommissionRule = commissionRuleService.queryCommissionRuleById(id);
        if (mtCommissionRule == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        MtCommissionRule commissionRule = new MtCommissionRule();
        commissionRule.setOperator(operator);
        commissionRule.setId(id);
        commissionRule.setStatus(status);
        commissionRuleService.updateCommissionRule(commissionRule);

        return getSuccessResult(true);
    }

    /**
     * 保存分销提成规则
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtCommissionRule info = new MtCommissionRule();
        info.setName(name);
        info.setDescription(description);
        info.setOperator(accountInfo.getAccountName());
        info.setStatus(status);
        info.setStoreId(Integer.parseInt(storeId));
        if (StringUtil.isNotEmpty(id)) {
            info.setId(Integer.parseInt(id));
            commissionRuleService.updateCommissionRule(info);
        } else {
            commissionRuleService.addCommissionRule(info);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取分销提成规则详情
     *
     * @param  id
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtCommissionRule commissionRule = commissionRuleService.queryCommissionRuleById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("commissionRule", commissionRule);

        return getSuccessResult(result);
    }
}
