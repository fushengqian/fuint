package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.BookDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BookDetailParam;
import com.fuint.common.param.BookListParam;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
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
     * 获取预约项目列表
     */
    @ApiOperation(value="获取预约项目列表", notes="获取预约项目列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request,  @RequestBody BookListParam param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String name = param.getName();
        Integer cateId = param.getCateId();
        Integer page = param.getPage() == null ? Constants.PAGE_NUMBER : param.getPage();
        Integer pageSize = param.getPageSize() == null ? Constants.PAGE_SIZE : param.getPageSize();
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(merchantNo)) {
            params.put("merchantNo", merchantNo);
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
        mtBookItem.setServiceDate(DateUtil.parseDate(date));
        mtBookItem.setServiceTime(time);
        MtBookItem result = bookItemService.addBookItem(mtBookItem);

        return getSuccessResult(result);
    }
}
