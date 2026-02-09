package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CommissionRelationPage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.CommissionRelationService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtCommissionRelation;
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
     */
    @ApiOperation(value = "分销提成邀请记录查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject list(@ModelAttribute CommissionRelationPage commissionRelationPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionRelationPage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionRelationPage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<CommissionRelationDto> paginationResponse = commissionRelationService.queryRelationByPagination(commissionRelationPage);

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
     */
    @ApiOperation(value = "更新邀请记录状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtCommissionRelation mtCommissionRelation = commissionRelationService.getById(params.getId());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(mtCommissionRelation.getMerchantId())) {
                return getFailureResult(1004);
            }
        }
        mtCommissionRelation.setStatus(params.getStatus());
        commissionRelationService.updateById(mtCommissionRelation);
        return getSuccessResult(true);
    }

    /**
     * 删除分销提成邀请记录
     */
    @ApiOperation(value = "删除分销提成邀请记录")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
