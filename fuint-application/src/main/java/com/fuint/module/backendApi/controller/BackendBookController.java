package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BookDto;
import com.fuint.common.dto.BookTimeDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BookPage;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.BookService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBook;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约项目管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-预约相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/book")
public class BackendBookController extends BaseController {

    /**
     * 预约服务接口
     */
    private BookService bookService;

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
     * 预约项目列表查询
     */
    @ApiOperation(value = "预约项目列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(@ModelAttribute BookPage bookPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bookPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bookPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<BookDto> paginationResponse = bookService.queryBookListByPagination(bookPage);

        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());
        List<MtBookCate> cateList = bookCateService.getAvailableBookCate(accountInfo.getMerchantId(), accountInfo.getStoreId());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约项目状态
     */
    @ApiOperation(value = "更新预约项目状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException, ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        BookDto bookDto = bookService.getBookById(id, false);
        if (bookDto == null) {
            return getFailureResult(201);
        }

        MtBook mtBook = new MtBook();
        BeanUtils.copyProperties(bookDto, mtBook);

        mtBook.setOperator(accountInfo.getAccountName());
        mtBook.setStatus(status);
        bookService.updateBook(mtBook);

        return getSuccessResult(true);
    }

    /**
     * 保存预约项目
     */
    @ApiOperation(value = "保存预约项目")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(@RequestBody BookDto bookDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            getFailureResult(5002);
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bookDto.setStoreId(accountInfo.getStoreId());
        }

        MtBook mtBook = new MtBook();
        BeanUtils.copyProperties(bookDto, mtBook);
        mtBook.setMerchantId(accountInfo.getMerchantId());
        mtBook.setOperator(accountInfo.getAccountName());
        mtBook.setServiceDates(bookDto.getDates());
        String timeStr = "";
        if (bookDto.getTimes() != null && bookDto.getTimes().size() > 0) {
            List<String> timeArr = new ArrayList<>();
            for (BookTimeDto time : bookDto.getTimes()) {
                 if (StringUtil.isNotEmpty(time.getStartTime()) && StringUtil.isNotEmpty(time.getEndTime()) && StringUtil.isNotEmpty(time.getNum())) {
                     String item = time.getStartTime() + "-" + time.getEndTime() + "-" + time.getNum();
                     if (!timeArr.contains(item)) {
                         timeArr.add(item);
                     }
                 }
            }
            if (timeArr.size() > 0) {
                timeStr = timeArr.stream().collect(Collectors.joining(","));
                mtBook.setServiceTimes(timeStr);
            }
        }
        mtBook.setServiceTimes(timeStr);
        if (bookDto.getId() != null) {
            bookService.updateBook(mtBook);
        } else {
            bookService.addBook(mtBook);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约项目详情
     */
    @ApiOperation(value = "获取预约项目详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException, ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        BookDto bookDto = bookService.getBookById(id, false);

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!bookDto.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookInfo", bookDto);

        return getSuccessResult(result);
    }
}
