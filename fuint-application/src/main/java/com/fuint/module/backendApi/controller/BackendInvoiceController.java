package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.InvoiceService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtInvoice;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 发票管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-发票相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/invoice")
public class BackendInvoiceController extends BaseController {

    /**
     * 发票服务接口
     */
    private InvoiceService invoiceService;

    /**
     * 发票列表查询
     */
    @ApiOperation(value = "发票列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('invoice:list')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        Integer storeId = accountInfo.getStoreId();

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
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
        PaginationResponse<MtInvoice> paginationResponse = invoiceService.queryInvoiceListByPagination(new PaginationRequest(page, pageSize, params));

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 更新发票状态
     *
     * @return
     */
    @ApiOperation(value = "更新发票状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('invoice:edit')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        MtInvoice mtInvoice = invoiceService.queryInvoiceById(id);
        if (mtInvoice == null) {
            return getFailureResult(201);
        }

        mtInvoice.setOperator(accountInfo.getAccountName());
        mtInvoice.setStatus(status);
        invoiceService.updateInvoice(mtInvoice);

        return getSuccessResult(true);
    }

    /**
     * 保存发票
     */
    @ApiOperation(value = "保存发票")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('invoice:add')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));

        MtInvoice mtInvoice = new MtInvoice();
        mtInvoice.setOperator(accountInfo.getAccountName());
        mtInvoice.setStatus(status);
        mtInvoice.setStoreId(Integer.parseInt(storeId));
        mtInvoice.setMerchantId(accountInfo.getMerchantId());
        if (StringUtil.isNotEmpty(id)) {
            mtInvoice.setId(Integer.parseInt(id));
            invoiceService.updateInvoice(mtInvoice);
        } else {
            invoiceService.addInvoice(mtInvoice);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取发票详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取发票详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('invoice:list')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtInvoice invoiceInfo = invoiceService.queryInvoiceById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("invoiceInfo", invoiceInfo);

        return getSuccessResult(result);
    }
}
