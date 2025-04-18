package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.service.CommissionRelationService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtCommissionRelation;
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
 * 分销提成邀请记录管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-分销提成邀请记录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionRelation")
public class BackendCommissionRelationController extends BaseController {

    /**
     * 分销提成邀请记录业务接口
     */
    private CommissionRelationService commissionRelationService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 分销提成邀请记录列表查询
     *
     * @param request HttpServletRequest对象
     * @return 分销提成邀请记录
     */
    @ApiOperation(value = "分销提成邀请记录查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String subUserId = request.getParameter("subUserId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(userId)) {
            params.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(subUserId)) {
            params.put("subUserId", subUserId);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<CommissionRelationDto> paginationResponse = commissionRelationService.queryRelationByPagination(paginationRequest);

        Map<String, Object> param = new HashMap<>();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            param.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新邀请记录状态
     *
     * @return
     */
    @ApiOperation(value = "更新邀请记录状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer id = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        MtCommissionRelation mtCommissionRelation = commissionRelationService.getById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(mtCommissionRelation.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        mtCommissionRelation.setStatus(status);
        commissionRelationService.updateById(mtCommissionRelation);

        return getSuccessResult(true);
    }

    /**
     * 删除分销提成邀请记录
     *
     * @param  id 邀请记录ID
     * @return
     */
    @ApiOperation(value = "删除分销提成邀请记录")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtCommissionRelation mtCommissionRelation = commissionRelationService.getById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(mtCommissionRelation.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        mtCommissionRelation.setStatus(StatusEnum.DISABLE.getKey());
        commissionRelationService.updateById(mtCommissionRelation);

        return getSuccessResult(true);
    }
}
