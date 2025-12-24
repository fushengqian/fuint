package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BookItemPage;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.BookItemService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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
     */
    @ApiOperation(value = "预约订单列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(@ModelAttribute BookItemPage bookItemPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bookItemPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bookItemPage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<BookItemDto> paginationResponse = bookItemService.queryBookItemListByPagination(bookItemPage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());
        List<ParamDto> bookStatusList = BookStatusEnum.getBookStatusList(BookStatusEnum.DELETE.getKey());
        List<MtBookCate> cateList = bookCateService.getAvailableBookCate(accountInfo.getMerchantId(), accountInfo.getStoreId());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);
        result.put("bookStatusList", bookStatusList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约订单状态
     */
    @ApiOperation(value = "更新预约订单状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBookItem mtBookItem = bookItemService.getBookItemById(id);
        if (mtBookItem == null) {
            return getFailureResult(201);
        }

        mtBookItem.setOperator(accountInfo.getAccountName());
        mtBookItem.setStatus(status);
        bookItemService.updateBookItem(mtBookItem);

        return getSuccessResult(true);
    }

    /**
     * 保存预约订单
     */
    @ApiOperation(value = "保存预约订单")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(@RequestBody BookItemDto bookItemDto) throws BusinessCheckException, ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtBookItem mtBookItem = new MtBookItem();
        BeanUtils.copyProperties(bookItemDto, mtBookItem);
        mtBookItem.setOperator(accountInfo.getAccountName());
        mtBookItem.setMerchantId(accountInfo.getMerchantId());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            mtBookItem.setStoreId(accountInfo.getStoreId());
        }
        if (bookItemDto.getId() != null && mtBookItem.getId() > 0) {
            bookItemService.updateBookItem(mtBookItem);
        } else {
            bookItemService.addBookItem(mtBookItem);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约订单详情
     */
    @ApiOperation(value = "获取预约订单详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBookItem mtBookItem = bookItemService.getBookItemById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !mtBookItem.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("mtBookItem", mtBookItem);
        return getSuccessResult(result);
    }
}
