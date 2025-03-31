package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.BookItemService;
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
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约订单管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-预约订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/bookItem")
public class BackendBookItemController extends BaseController {

    /**
     * 预约订单服务接口
     */
    private BookItemService bookItemService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 预约分类服务接口
     */
    private BookCateService bookCateService;

    /**
     * 预约订单列表查询
     *
     * @param  request HttpServletRequest对象
     * @return 预约订单列表
     */
    @ApiOperation(value = "预约订单列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String mobile = request.getParameter("mobile");
        String contact = request.getParameter("contact");
        String status = request.getParameter("status");
        String userId = request.getParameter("userId");
        String cateId = request.getParameter("cateId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId = accountInfo.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(cateId)) {
            params.put("cateId", cateId);
        }
        if (StringUtil.isNotEmpty(contact)) {
            params.put("contact", contact);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<BookItemDto> paginationResponse = bookItemService.queryBookItemListByPagination(paginationRequest);

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

        // 预约状态列表
        BookStatusEnum[] bookStatusEnum = BookStatusEnum.values();
        List<ParamDto> bookStatusList = new ArrayList<>();
        for (BookStatusEnum enumItem : bookStatusEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            bookStatusList.add(paramDto);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            param.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtBookCate> cateList = bookCateService.queryBookCateListByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);
        result.put("bookStatusList", bookStatusList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约订单状态
     *
     * @return
     */
    @ApiOperation(value = "更新预约订单状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtBookItem mtBookItem = bookItemService.getBookItemById(id);
        if (mtBookItem == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        mtBookItem.setOperator(operator);
        mtBookItem.setStatus(status);
        bookItemService.updateBookItem(mtBookItem);

        return getSuccessResult(true);
    }

    /**
     * 保存预约订单
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存预约订单")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String mobile = params.get("name") == null ? "" : params.get("name").toString();
        String remark = params.get("remark") == null ? "" : params.get("remark").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtBookItem mtBookItem = new MtBookItem();
        mtBookItem.setMobile(mobile);
        mtBookItem.setRemark(remark);
        mtBookItem.setOperator(accountInfo.getAccountName());
        mtBookItem.setStatus(status);
        mtBookItem.setStoreId(Integer.parseInt(storeId));
        mtBookItem.setMerchantId(accountInfo.getMerchantId());

        if (StringUtil.isNotEmpty(id)) {
            bookItemService.updateBookItem(mtBookItem);
        } else {
            bookItemService.addBookItem(mtBookItem);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约订单详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取预约订单详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtBookItem mtBookItem = bookItemService.getBookItemById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !mtBookItem.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mtBookItem", mtBookItem);

        return getSuccessResult(result);
    }
}
