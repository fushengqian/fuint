package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtBookCate;
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
 * 预约分类管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-预约分类相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/bookCate")
public class BackendBookCateController extends BaseController {

    /**
     * 预约分类服务接口
     */
    private BookCateService bookCateService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 预约分类列表查询
     *
     * @param  request HttpServletRequest对象
     * @return 预约分类列表
     */
    @ApiOperation(value = "预约分类列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId = accountInfo.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
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
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtBookCate> paginationResponse = bookCateService.queryBookCateListByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }

        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约分类状态
     *
     * @return
     */
    @ApiOperation(value = "更新预约分类状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer cateId = params.get("cateId") == null ? 0 : Integer.parseInt(params.get("cateId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtBookCate mtBookCate = bookCateService.getBookCateById(cateId);
        if (mtBookCate == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        mtBookCate.setOperator(operator);
        mtBookCate.setStatus(status);
        bookCateService.updateBookCate(mtBookCate);

        return getSuccessResult(true);
    }

    /**
     * 保存预约分类
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存预约分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String logo = params.get("logo") == null ? "" : params.get("logo").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? "0" : params.get("storeId").toString();
        String sort = (params.get("sort") == null || StringUtil.isEmpty(params.get("sort").toString())) ? "0" : params.get("sort").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        MtBookCate mtBookCate = new MtBookCate();
        mtBookCate.setName(name);
        mtBookCate.setDescription(description);
        mtBookCate.setLogo(logo);
        mtBookCate.setOperator(accountInfo.getAccountName());
        mtBookCate.setStoreId(Integer.parseInt(storeId));
        mtBookCate.setMerchantId(accountInfo.getMerchantId());
        mtBookCate.setSort(Integer.parseInt(sort));
        mtBookCate.setStatus(status);

        if (StringUtil.isNotEmpty(id)) {
            mtBookCate.setId(Integer.parseInt(id));
            bookCateService.updateBookCate(mtBookCate);
        } else {
            bookCateService.addBookCate(mtBookCate);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约分类详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取预约分类详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtBookCate bookCateInfo = bookCateService.getBookCateById(id);
        String imagePath = settingService.getUploadBasePath();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!bookCateInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookCateInfo", bookCateInfo);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }
}
