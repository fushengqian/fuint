package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.CommissionRuleDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CommissionTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CommissionRuleParam;
import com.fuint.common.service.CommissionRuleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtCommissionRule;
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
 * 分销提成规则管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-分销提成规则相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionRule")
public class BackendCommissionRuleController extends BaseController {

    /**
     * 分销提成规则服务接口
     */
    private CommissionRuleService commissionRuleService;

    /**
     * 规则列表查询
     */
    @ApiOperation(value = "规则列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String target = request.getParameter("target");
        String type = request.getParameter("type");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer storeId = accountInfo.getStoreId();

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(target)) {
            params.put("target", target);
        }
        if (StringUtil.isNotEmpty(type)) {
            params.put("type", type);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        PaginationResponse<MtCommissionRule> paginationResponse = commissionRuleService.queryDataByPagination(new PaginationRequest(page, pageSize, params));

        // 分佣提成类型列表
        List<ParamDto> typeList = CommissionTypeEnum.getCommissionTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 更新分销提成规则状态
     */
    @ApiOperation(value = "更新分销提成规则状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        CommissionRuleDto commissionRuleDto = commissionRuleService.queryCommissionRuleById(id);
        if (commissionRuleDto == null) {
            return getFailureResult(201);
        }

        CommissionRuleParam commissionRule = new CommissionRuleParam();
        commissionRule.setOperator(accountInfo.getAccountName());
        commissionRule.setId(id);
        commissionRule.setStatus(status);
        commissionRuleService.updateCommissionRule(commissionRule);

        return getSuccessResult(true);
    }

    /**
     * 保存分销提成规则
     */
    @ApiOperation(value = "保存分销提成规则")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject saveHandler(@RequestBody CommissionRuleParam commissionRule) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionRule.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionRule.setStoreId(accountInfo.getStoreId());
        }
        commissionRule.setOperator(accountInfo.getAccountName());
        if (commissionRule.getId() != null && commissionRule.getId() > 0) {
            commissionRuleService.updateCommissionRule(commissionRule);
        } else {
            commissionRuleService.addCommissionRule(commissionRule);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取分销提成规则详情
     */
    @ApiOperation(value = "获取分销提成规则详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        CommissionRuleDto commissionRule = commissionRuleService.queryCommissionRuleById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("commissionRule", commissionRule);
        return getSuccessResult(result);
    }
}
