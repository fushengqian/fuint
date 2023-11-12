package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettlementService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtSettlement;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商户结算管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商户结算相关接口")
@RestController
@RequestMapping(value = "/backendApi/settlement")
public class BackendSettlementController extends BaseController {

    /**
     * 结算服务接口
     * */
    @Autowired
    private SettlementService settlementService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 商户服务接口
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * 结算列表查询
     *
     * @param  request HttpServletRequest对象
     * @return 余额明细列表
     */
    @ApiOperation(value = "结算列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(mobile)) {
            searchParams.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(userId)) {
            searchParams.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }
        Integer storeId = accountInfo.getStoreId();
        if (storeId != null && storeId > 0) {
            searchParams.put("storeId", storeId);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(params);
        List<MtMerchant> merchantList = merchantService.queryMerchantByParams(params);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        paginationRequest.setSearchParams(searchParams);

        PaginationResponse<MtSettlement> paginationResponse = settlementService.querySettlementListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("merchantList", merchantList);
        result.put("storeList", storeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 提交结算
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "提交结算")
    @RequestMapping(value = "/doSubmit", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:doSubmit')")
    public ResponseObject doSubmit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantId = StringUtil.isEmpty(param.get("merchantId").toString())? "0" : param.get("merchantId").toString();
        String storeId = StringUtil.isEmpty(param.get("storeId").toString()) ? "0" : param.get("storeId").toString();
        String remark = param.get("remark") == null ? "后台发起结算" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        MtSettlement mtSettlement = new MtSettlement();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtSettlement.setMerchantId(accountInfo.getMerchantId());
        } else {
            mtSettlement.setMerchantId(Integer.parseInt(merchantId));
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            mtSettlement.setStoreId(accountInfo.getStoreId());
        } else {
            mtSettlement.setStoreId(Integer.parseInt(storeId));
        }
        mtSettlement.setDescription(remark);
        mtSettlement.setOperator(operator);

        settlementService.submitSettlement(mtSettlement);
        return getSuccessResult(true);
    }
}
