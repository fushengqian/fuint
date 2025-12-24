package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BookCateDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BookCatePage;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     */
    @ApiOperation(value = "预约分类列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(@ModelAttribute BookCatePage bookCatePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bookCatePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bookCatePage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtBookCate> paginationResponse = bookCateService.queryBookCateListByPagination(bookCatePage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约分类状态
     */
    @ApiOperation(value = "更新预约分类状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer cateId = params.get("cateId") == null ? 0 : Integer.parseInt(params.get("cateId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBookCate mtBookCate = bookCateService.getBookCateById(cateId);
        if (mtBookCate == null) {
            return getFailureResult(201);
        }

        mtBookCate.setOperator(accountInfo.getAccountName());
        mtBookCate.setStatus(status);
        bookCateService.updateBookCate(mtBookCate);

        return getSuccessResult(true);
    }

    /**
     * 保存预约分类
     */
    @ApiOperation(value = "保存预约分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(@RequestBody BookCateDto bookCateDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            return getFailureResult(5002);
        }

        MtBookCate mtBookCate = new MtBookCate();
        BeanUtils.copyProperties(bookCateDto, mtBookCate);
        mtBookCate.setOperator(accountInfo.getAccountName());
        mtBookCate.setMerchantId(accountInfo.getMerchantId());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            mtBookCate.setStoreId(accountInfo.getStoreId());
        }
        if (bookCateDto.getId() != null && bookCateDto.getId() > 0) {
            bookCateService.updateBookCate(mtBookCate);
        } else {
            bookCateService.addBookCate(mtBookCate);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约分类详情
     */
    @ApiOperation(value = "获取预约分类详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBookCate bookCateInfo = bookCateService.getBookCateById(id);

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!bookCateInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookCateInfo", bookCateInfo);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }
}
