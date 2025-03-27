package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BookDto;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.BookService;
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
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;
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
     *
     * @param  request HttpServletRequest对象
     * @return 预约项目列表
     */
    @ApiOperation(value = "预约项目列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String cateId = request.getParameter("cateId");
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
        if (StringUtil.isNotEmpty(cateId)) {
            params.put("cateId", cateId);
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
        PaginationResponse<BookDto> paginationResponse = bookService.queryBookListByPagination(paginationRequest);

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            param.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }

        List<MtStore> storeList = storeService.queryStoresByParams(param);
        String imagePath = settingService.getUploadBasePath();
        List<MtBookCate> cateList = bookCateService.queryBookCateListByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 更新预约项目状态
     *
     * @return
     */
    @ApiOperation(value = "更新预约项目状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException, ParseException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        BookDto bookDto = bookService.getBookById(id);
        if (bookDto == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();
        MtBook mtBook = new MtBook();
        BeanUtils.copyProperties(bookDto, mtBook);

        mtBook.setOperator(operator);
        mtBook.setStatus(status);
        bookService.updateBook(mtBook);

        return getSuccessResult(true);
    }

    /**
     * 保存预约项目
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存预约项目")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String cateId = params.get("cateId") == null ? "0" : params.get("cateId").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String logo = params.get("logo") == null ? "" : params.get("logo").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? "0" : params.get("storeId").toString();
        String sort = (params.get("sort") == null || StringUtil.isEmpty(params.get("sort").toString())) ? "0" : params.get("sort").toString();
        String dates = params.get("dates") == null ? "" : params.get("dates").toString();
        List<LinkedHashMap> times = params.get("times") == null ? new ArrayList<>() : (List) params.get("times");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        MtBook mtBook = new MtBook();
        mtBook.setName(name);
        mtBook.setDescription(description);
        mtBook.setLogo(logo);
        mtBook.setOperator(accountInfo.getAccountName());
        mtBook.setStatus(status);
        mtBook.setStoreId(Integer.parseInt(storeId));
        mtBook.setSort(Integer.parseInt(sort));
        mtBook.setMerchantId(accountInfo.getMerchantId());
        mtBook.setServiceDates(dates);
        if (StringUtil.isNotEmpty(cateId)) {
            mtBook.setCateId(Integer.parseInt(cateId));
        }
        String timeStr = "";
        if (times != null && times.size() > 0) {
            List<String> timeArr = new ArrayList<>();
            for (LinkedHashMap time : times) {
                 String startTime = time.get("startTime") == null ? "" : time.get("startTime").toString();
                 String endTime = time.get("endTime") == null ? "" : time.get("endTime").toString();
                 String num = time.get("num") == null ? "" : time.get("num").toString();
                 if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime) && StringUtil.isNotEmpty(num)) {
                     String item = startTime + "-" + endTime + "-" + num;
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
        if (StringUtil.isNotEmpty(id)) {
            mtBook.setId(Integer.parseInt(id));
            bookService.updateBook(mtBook);
        } else {
            bookService.addBook(mtBook);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取预约项目详情
     *
     * @param id 预约项目ID
     * @return
     */
    @ApiOperation(value = "获取预约项目详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('book:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException, ParseException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        BookDto bookDto = bookService.getBookById(id);

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
