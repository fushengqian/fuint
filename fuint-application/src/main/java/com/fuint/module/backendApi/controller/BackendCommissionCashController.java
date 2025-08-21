package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionCashDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CommissionCashStatusEnum;
import com.fuint.common.service.CommissionCashService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.module.backendApi.request.CommissionCashRequest;
import com.fuint.module.backendApi.request.CommissionSettleConfirmRequest;
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
 * 分销提成提现管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-分销提成提现相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionCash")
public class BackendCommissionCashController extends BaseController {

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 分销提成提现业务接口
     */
    private CommissionCashService commissionCashService;

    /**
     * 分销提成提现记录列表
     */
    @ApiOperation(value = "分销提成提现记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String realName = request.getParameter("realName");
        String mobile = request.getParameter("mobile");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String uuid = request.getParameter("uuid");
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        Integer storeId = accountInfo.getStoreId();

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(realName)) {
            params.put("realName", realName);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
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
        if (StringUtil.isNotEmpty(uuid)) {
            params.put("uuid", uuid);
        }
        if (StringUtil.isNotEmpty(startTime)) {
            params.put("startTime", startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        PaginationResponse<CommissionCashDto> paginationResponse = commissionCashService.queryCommissionCashByPagination(new PaginationRequest(page, pageSize, params));

        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        // 状态列表
        List<ParamDto> statusList = CommissionCashStatusEnum.getCommissionCashStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);
        result.put("statusList", statusList);

        return getSuccessResult(result);
    }

    /**
     * 获取分销提成记录详情
     */
    @ApiOperation(value = "获取分销提成提现详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));

        CommissionCashDto commissionCash = commissionCashService.queryCommissionCashById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(commissionCash.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("commissionCash", commissionCash);

        return getSuccessResult(result);
    }

    /**
     * 修改分销提成提现
     */
    @ApiOperation(value = "修改分销提成提现")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject save(HttpServletRequest request, @RequestBody CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        commissionCashRequest.setOperator(accountDto.getAccountName());
        commissionCashService.updateCommissionCash(commissionCashRequest);
        return getSuccessResult(true);
    }

    /**
     * 结算确认
     */
    @ApiOperation(value = "结算确认")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject confirm(HttpServletRequest request, @RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        requestParam.setOperator(accountDto.getAccountName());
        if (accountDto.getMerchantId() != null && accountDto.getMerchantId() > 0) {
            requestParam.setMerchantId(accountDto.getMerchantId());
        }
        commissionCashService.confirmCommissionCash(requestParam);
        return getSuccessResult(true);
    }

    /**
     * 取消结算
     */
    @ApiOperation(value = "取消结算")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject cancel(HttpServletRequest request, @RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        if (accountDto.getMerchantId() != null && accountDto.getMerchantId() > 0) {
            requestParam.setMerchantId(accountDto.getMerchantId());
        }
        requestParam.setOperator(accountDto.getAccountName());
        commissionCashService.cancelCommissionCash(requestParam);
        return getSuccessResult(true);
    }
}
