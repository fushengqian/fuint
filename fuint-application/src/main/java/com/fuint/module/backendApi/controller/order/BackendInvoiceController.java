package com.fuint.module.backendApi.controller.order;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.InvoicePage;
import com.fuint.common.param.InvoiceParam;
import com.fuint.common.service.InvoiceService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtInvoice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseObject list(@ModelAttribute InvoicePage invoicePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            invoicePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            invoicePage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtInvoice> paginationResponse = invoiceService.queryInvoiceListByPagination(invoicePage);

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
    public ResponseObject updateStatus(@RequestBody InvoiceParam invoice) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtInvoice mtInvoice = invoiceService.queryInvoiceById(invoice.getId());
        if (mtInvoice == null) {
            return getFailureResult(201, "发票信息不存在");
        }
        if (!mtInvoice.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(201, "抱歉，您没有操作权限");
        }
        invoice.setOperator(accountInfo.getAccountName());
        invoiceService.updateInvoice(invoice);
        return getSuccessResult(true);
    }

    /**
     * 保存发票
     */
    @ApiOperation(value = "保存发票")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('invoice:add')")
    public ResponseObject saveHandler(@RequestBody InvoiceParam invoice) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }
        invoice.setStoreId(accountInfo.getStoreId());
        invoice.setMerchantId(accountInfo.getMerchantId());
        invoice.setOperator(accountInfo.getAccountName());
        if (invoice.getId() != null && invoice.getId() > 0) {
            invoiceService.updateInvoice(invoice);
        } else {
            invoiceService.addInvoice(invoice);
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
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtInvoice invoiceInfo = invoiceService.queryInvoiceById(id);
        if (invoiceInfo == null) {
            return getFailureResult(201, "发票信息不存在");
        }
        if (!invoiceInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(201, "抱歉，您没有查看权限");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("invoiceInfo", invoiceInfo);

        return getSuccessResult(result);
    }
}
