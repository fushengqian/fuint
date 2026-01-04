package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionCashDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CommissionCashStatusEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CommissionCashPage;
import com.fuint.common.service.CommissionCashService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.backendApi.request.CommissionCashRequest;
import com.fuint.module.backendApi.request.CommissionSettleConfirmRequest;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseObject list(@ModelAttribute CommissionCashPage commissionCashPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionCashPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionCashPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<CommissionCashDto> paginationResponse = commissionCashService.queryCommissionCashByPagination(commissionCashPage);

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
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
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
    public ResponseObject save(@RequestBody CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        commissionCashRequest.setOperator(accountInfo.getAccountName());
        if (!checkOwner(commissionCashRequest.getId(), accountInfo)) {
            return getFailureResult(1004);
        }
        commissionCashService.updateCommissionCash(commissionCashRequest);
        return getSuccessResult(true);
    }

    /**
     * 支付结算金额到用户余额
     */
    @ApiOperation(value = "支付结算金额到用户余额")
    @RequestMapping(value = "/payToBalance", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject payToBalance(@RequestBody CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        commissionCashRequest.setOperator(accountInfo.getAccountName());
        if (!checkOwner(commissionCashRequest.getId(), accountInfo)) {
            return getFailureResult(1004);
        }
        commissionCashService.payToBalance(commissionCashRequest);
        return getSuccessResult(true);
    }

    /**
     * 结算确认
     */
    @ApiOperation(value = "结算确认")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject confirm(@RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        AccountInfo AccountInfo = TokenUtil.getAccountInfo();
        requestParam.setOperator(AccountInfo.getAccountName());
        if (AccountInfo.getMerchantId() != null && AccountInfo.getMerchantId() > 0) {
            requestParam.setMerchantId(AccountInfo.getMerchantId());
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
    public ResponseObject cancel(@RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            requestParam.setMerchantId(accountInfo.getMerchantId());
        }
        requestParam.setOperator(accountInfo.getAccountName());
        commissionCashService.cancelCommissionCash(requestParam);
        return getSuccessResult(true);
    }

    private boolean checkOwner(Integer commissionCashId, AccountInfo accountInfo) throws BusinessCheckException {
        CommissionCashDto commissionCash = commissionCashService.queryCommissionCashById(commissionCashId);
        if (commissionCash == null) {
            return false;
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(commissionCash.getMerchantId())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
