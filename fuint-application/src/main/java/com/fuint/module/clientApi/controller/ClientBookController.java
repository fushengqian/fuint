package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.BookDto;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BookDetailParam;
import com.fuint.common.param.BookListParam;
import com.fuint.common.param.BookableParam;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-预约相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/book")
public class ClientBookController extends BaseController {

    /**
     * 预约项目服务接口
     * */
    private BookService bookService;

    /**
     * 预约项目分类服务接口
     * */
    private BookCateService bookCateService;

    /**
     * 预约记录服务接口
     * */
    private BookItemService bookItemService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 获取预约项目列表
     */
    @ApiOperation(value="获取预约项目列表", notes="获取预约项目列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody BookListParam param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String name = param.getName();
        Integer cateId = param.getCateId();
        Integer page = param.getPage() == null ? Constants.PAGE_NUMBER : param.getPage();
        Integer pageSize = param.getPageSize() == null ? Constants.PAGE_SIZE : param.getPageSize();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        Integer merchantId = 0;
        if (StringUtil.isNotEmpty(merchantNo)) {
            merchantId = merchantService.getMerchantId(merchantNo);
        } else if (storeId != null && storeId > 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null && mtStore.getMerchantId() != null) {
                merchantId = mtStore.getMerchantId();
            }
        }
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }
        if (cateId != null && cateId > 0) {
            params.put("cateId", cateId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<BookDto> paginationResponse = bookService.queryBookListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap();
        result.put("content", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(result);
    }

    /**
     * 获取预约项目详情
     */
    @ApiOperation(value="获取预约项目详情", notes="根据ID获取预约项目详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody BookDetailParam param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException, ParseException {
        Integer bookId = param.getBookId() == null ? 0 : param.getBookId();

        BookDto bookInfo = bookService.getBookById(bookId);
        Map<String, Object> result = new HashMap<>();
        result.put("bookInfo", bookInfo);

        return getSuccessResult(result);
    }

    /**
     * 获取预约分类列表
     */
    @ApiOperation(value = "获取预约分类列表")
    @RequestMapping(value = "/cateList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cateList(HttpServletRequest request) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            param.put("merchantId", merchantId);
        }
        if (storeId > 0) {
            param.put("storeId", storeId);
        }
        List<MtBookCate> cateList = bookCateService.queryBookCateListByParams(param);
        Map<String, Object> result = new HashMap<>();
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 是否可预约
     */
    @ApiOperation(value="获取预约项目详情", notes="根据ID获取预约项目详情")
    @RequestMapping(value = "/bookable", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject bookable(@RequestBody BookableParam param) throws BusinessCheckException {
        List<String> result = bookService.isBookable(param);
        return getSuccessResult(result);
    }

    /**
     * 预约提交
     */
    @ApiOperation(value = "预约提交")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException, ParseException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String bookId = param.get("bookId") == null ? "" : param.get("bookId").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String contact = param.get("contact") == null ? "" : param.get("contact").toString();
        String date = param.get("date") == null ? "" : param.get("date").toString();
        String time = param.get("time") == null ? "" : param.get("time").toString();

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        if (null == loginInfo) {
            return getFailureResult(1001);
        }

        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        BookDto bookInfo = bookService.getBookById(Integer.parseInt(bookId));
        if (bookInfo == null) {
            return getFailureResult(2001);
        }

        MtBookItem mtBookItem = new MtBookItem();
        mtBookItem.setCateId(bookInfo.getCateId());
        mtBookItem.setUserId(mtUser.getId());
        mtBookItem.setRemark(remark);
        mtBookItem.setMerchantId(mtUser.getMerchantId());
        mtBookItem.setStoreId(storeId);
        mtBookItem.setMobile(mobile);
        mtBookItem.setContact(contact);
        mtBookItem.setBookId(bookInfo.getId());
        mtBookItem.setServiceDate(date);
        mtBookItem.setServiceTime(time);
        MtBookItem result = bookItemService.addBookItem(mtBookItem);

        return getSuccessResult(result);
    }

    /**
     * 获取我的预约
     */
    @ApiOperation(value = "获取我的预约")
    @RequestMapping(value = "/myBook", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject myBook(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");

        Map<String, Object> param = new HashMap<>();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            param.put("merchantId", merchantId);
        }

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        if (null == loginInfo) {
            return getFailureResult(1001);
        }
        param.put("userId", loginInfo.getId());
        if (StringUtil.isNotEmpty(status)) {
            param.put("status", status);
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        paginationRequest.setSearchParams(param);
        PaginationResponse<BookItemDto> paginationResponse = bookItemService.queryBookItemListByPagination(paginationRequest);

        // 预约状态列表
        BookStatusEnum[] enums = BookStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (BookStatusEnum enumItem : enums) {
            if (!enumItem.getKey().equals(BookStatusEnum.DELETE.getKey())) {
                ParamDto paramDto = new ParamDto();
                paramDto.setKey(enumItem.getKey());
                paramDto.setName(enumItem.getValue());
                paramDto.setValue(enumItem.getKey());
                statusList.add(paramDto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());
        result.put("statusList", statusList);

        return getSuccessResult(result);
    }

    /**
     * 取消预约
     */
    @ApiOperation(value = "取消预约")
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String bookId = request.getParameter("bookId");
        String remark = request.getParameter("remark") == null ? "会员取消" : request.getParameter("remark");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        if (StringUtil.isEmpty(bookId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        MtBookItem bookItem = bookItemService.getBookItemById(Integer.parseInt(bookId));
        if (bookItem == null || !bookItem.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "预约信息有误");
        }

        Boolean result = bookItemService.cancelBook(bookItem.getId(), remark);
        return getSuccessResult(result);
    }

    /**
     * 获取我的预约详情
     */
    @ApiOperation(value="获取我的预约详情", notes="根据ID获取我的预约详情")
    @RequestMapping(value = "/myBookDetail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject myBookDetail(@RequestBody BookDetailParam param) throws BusinessCheckException {
        Integer bookId = param.getBookId() == null ? 0 : param.getBookId();

        BookItemDto bookInfo = bookItemService.getBookDetail(bookId);
        Map<String, Object> result = new HashMap<>();
        result.put("bookInfo", bookInfo);

        return getSuccessResult(result);
    }
}
