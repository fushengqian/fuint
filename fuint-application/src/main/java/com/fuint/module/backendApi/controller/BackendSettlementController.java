package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.SettlementDto;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.SettleStatusEnum;
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
import com.fuint.module.backendApi.request.SettlementRequest;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtSettlement;
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
 * 商户结算管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商户结算相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/settlement")
public class BackendSettlementController extends BaseController {

    /**
     * 结算服务接口
     * */
    private SettlementService settlementService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 结算列表查询
     */
    @ApiOperation(value = "结算列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
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

        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());
        List<MtMerchant> merchantList = merchantService.getMyMerchantList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        PaginationResponse<MtSettlement> paginationResponse = settlementService.querySettlementListByPagination(new PaginationRequest(page, pageSize, searchParams));

        // 结算状态
        List<ParamDto> statusList = SettleStatusEnum.getSettleStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("merchantList", merchantList);
        result.put("storeList", storeList);
        result.put("statusList", statusList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 获取结算单详情
     * */
    @ApiOperation(value = "获取结算单详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:index')")
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        Integer settlementId = request.getParameter("settlementId") == null ? 0 : Integer.parseInt(request.getParameter("settlementId"));
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        SettlementDto settlementInfo = settlementService.getSettlementInfo(settlementId, page, pageSize);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(settlementInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        List<ParamDto> statusList = OrderStatusEnum.getOrderStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("settlementInfo", settlementInfo);
        result.put("statusList", statusList);

        return getSuccessResult(result);
    }

    /**
     * 提交结算
     */
    @ApiOperation(value = "提交结算")
    @RequestMapping(value = "/doSubmit", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:doSubmit')")
    public ResponseObject doSubmit(@RequestBody SettlementRequest requestParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            requestParam.setMerchantId(accountInfo.getMerchantId());
        }
        requestParam.setOperator(accountInfo.getAccountName());
        settlementService.submitSettlement(requestParam);

        return getSuccessResult(true);
    }

    /**
     * 确认结算
     */
    @ApiOperation(value = "确认结算")
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('settlement:doConfirm')")
    public ResponseObject doConfirm(@RequestBody SettlementRequest param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer settlementId = param.getSettlementId();
        if (settlementId == null) {
            return getFailureResult(201, "参数有误");
        }
        settlementService.doConfirm(settlementId, accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
