package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.BookDto;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.param.*;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));

        BookPage bookPage = new BookPage();
        bookPage.setName(param.getName());
        Integer merchantId = 0;
        if (StringUtil.isNotEmpty(merchantNo)) {
            merchantId = merchantService.getMerchantId(merchantNo);
        } else if (storeId != null && storeId > 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null && mtStore.getMerchantId() != null) {
                merchantId = mtStore.getMerchantId();
            }
        }
        bookPage.setMerchantId(merchantId);
        bookPage.setCateId(param.getCateId());
        bookPage.setPage(param.getPage());
        bookPage.setPageSize(param.getPageSize());
        PaginationResponse<BookDto> paginationResponse = bookService.queryBookListByPagination(bookPage);

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

        BookDto bookInfo = bookService.getBookById(bookId, true);
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
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        List<MtBookCate> cateList = bookCateService.getAvailableBookCate(merchantId, storeId);
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
    public ResponseObject bookable(@RequestBody BookableParam param) throws BusinessCheckException,ParseException {
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
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String bookId = param.get("bookId") == null ? "" : param.get("bookId").toString();
        String orderGoodsId = param.get("orderGoodsId") == null ? "" : param.get("orderGoodsId").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String contact = param.get("contact") == null ? "" : param.get("contact").toString();
        String date = param.get("date") == null ? "" : param.get("date").toString();
        String time = param.get("time") == null ? "" : param.get("time").toString();

        UserInfo loginInfo = TokenUtil.getUserInfo();
        if (null == loginInfo) {
            return getFailureResult(1001);
        }

        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        BookDto bookInfo = bookService.getBookById(Integer.parseInt(bookId), true);
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
        if (StringUtil.isNotBlank(orderGoodsId)) {
            mtBookItem.setGoodsId(Integer.parseInt(orderGoodsId));
        }
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
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        BookItemPage bookItemPage = new BookItemPage();
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        if (merchantId > 0) {
            bookItemPage.setMerchantId(merchantId);
        }
        UserInfo loginInfo = TokenUtil.getUserInfo();
        bookItemPage.setUserId(loginInfo.getId());
        if (StringUtil.isNotEmpty(status)) {
            bookItemPage.setStatus(status);
        }

        PaginationResponse<BookItemDto> paginationResponse = bookItemService.queryBookItemListByPagination(bookItemPage);

        Map<String, Object> result = new HashMap<>();
        result.put("content", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());
        result.put("statusList", BookStatusEnum.getBookStatusList(BookStatusEnum.DELETE.getKey()));

        return getSuccessResult(result);
    }

    /**
     * 取消预约
     */
    @ApiOperation(value = "取消预约")
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException {
        String bookId = request.getParameter("bookId");
        String remark = request.getParameter("remark") == null ? "会员取消" : request.getParameter("remark");
        UserInfo mtUser = TokenUtil.getUserInfo();

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
