package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionLogDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CommissionStatusEnum;
import com.fuint.common.enums.CommissionTargetEnum;
import com.fuint.common.service.CommissionCashService;
import com.fuint.common.service.CommissionLogService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.module.backendApi.request.CommissionLogRequest;
import com.fuint.module.backendApi.request.CommissionSettleRequest;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销提成记录管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-分销提成记录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionLog")
public class BackendCommissionLogController extends BaseController {

    /**
     * 分销提成记录业务接口
     */
    private CommissionLogService commissionLogService;

    /**
     * 分销提成提现业务接口
     */
    private CommissionCashService commissionCashService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 分销提成记录列表查询
     *
     * @param request HttpServletRequest对象
     * @return 分销提成记录
     */
    @ApiOperation(value = "分销提成记录查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:log:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String target = request.getParameter("target");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String realName = request.getParameter("realName");
        String mobile = request.getParameter("mobile");
        String uuid = request.getParameter("uuid");
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId = accountInfo.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(target)) {
            params.put("target", target);
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
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(startTime)) {
            params.put("startTime", startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        if (StringUtil.isNotEmpty(realName)) {
            params.put("realName", realName);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(uuid)) {
            params.put("uuid", uuid);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<CommissionLogDto> paginationResponse = commissionLogService.queryCommissionLogByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        // 状态列表
        List<ParamDto> statusList = CommissionStatusEnum.getCommissionStatusList();

        // 分佣对象列表
        List<ParamDto> targetList = CommissionTargetEnum.getCommissionTargetList();

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);
        result.put("statusList", statusList);
        result.put("targetList", targetList);

        return getSuccessResult(result);
    }

    /**
     * 获取分销提成记录详情
     *
     * @param  id
     * @return
     */
    @ApiOperation(value = "获取分销提成记录详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:log:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        CommissionLogDto commissionLog = commissionLogService.queryCommissionLogById(id);
        if (accountInfo.getMerchantId() > 0 && !commissionLog.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("commissionLog", commissionLog);

        return getSuccessResult(result);
    }

    /**
     * 修改分销提成记录
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "修改分销提成记录")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:log:index')")
    public ResponseObject save(HttpServletRequest request, @RequestBody CommissionLogRequest commissionLogRequest) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);

        commissionLogRequest.setOperator(accountDto.getAccountName());
        commissionLogService.updateCommissionLog(commissionLogRequest);

        return getSuccessResult(true);
    }

    /**
     * 作废分销提成记录
     *
     * @param  id
     * @return
     */
    @ApiOperation(value = "作废分销提成记录")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:log:index')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        CommissionLogDto commissionLog = commissionLogService.queryCommissionLogById(id);
        if (accountInfo.getMerchantId() > 0 && !commissionLog.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        CommissionLogRequest commissionLogRequest = new CommissionLogRequest();
        commissionLogRequest.setId(id);
        commissionLogRequest.setStatus(CommissionStatusEnum.CANCEL.getKey());
        commissionLogService.updateCommissionLog(commissionLogRequest);

        return getSuccessResult(true);
    }

    /**
     * 分销提成结算
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "分销提成结算")
    @RequestMapping(value = "/doSettle", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:log:index')")
    public ResponseObject doSettle(HttpServletRequest request, @RequestBody CommissionSettleRequest commissionSettleRequest) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);

        commissionSettleRequest.setOperator(accountDto.getAccountName());
        if (accountDto.getMerchantId() != null && accountDto.getMerchantId() > 0) {
            commissionSettleRequest.setMerchantId(accountDto.getMerchantId());
        }
        if (accountDto.getStoreId() != null && accountDto.getStoreId() > 0) {
            commissionSettleRequest.setStoreId(accountDto.getStoreId());
        }
        String settleNo = commissionCashService.settleCommission(commissionSettleRequest);
        return getSuccessResult(settleNo);
    }
}
