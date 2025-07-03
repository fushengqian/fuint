package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.service.BookItemService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.merchantApi.request.BookDetailParam;
import com.fuint.module.merchantApi.request.BookListRequest;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-预约管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/book")
public class MerchantBookController extends BaseController {

    /**
     * 预约单服务接口
     */
    private BookItemService bookItemService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 获取预约单列表
     */
    @ApiOperation(value = "获取预约单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody BookListRequest requestParams) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        Map<String, Object> params = new HashMap<>();

        if (staffInfo == null) {
            return getFailureResult(1004);
        } else {
            params.put("merchantId", staffInfo.getMerchantId());
            params.put("storeId", staffInfo.getStoreId());
        }
        if (StringUtil.isNotEmpty(requestParams.getStatus())) {
            params.put("status", requestParams.getStatus());
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(requestParams.getPage());
        paginationRequest.setPageSize(requestParams.getPageSize());
        paginationRequest.setSearchParams(params);

        PaginationResponse bookList = bookItemService.queryBookItemListByPagination(paginationRequest);
        return getSuccessResult(bookList);
    }

    /**
     * 获取预约单详情
     */
    @ApiOperation(value = "获取预约单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody BookDetailParam param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff mtStaff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (mtStaff == null) {
            return getFailureResult(1004);
        }

        Integer bookId = param.getBookId();
        if (bookId == null) {
            return getFailureResult(2000, "预约ID不能为空");
        }

        MtBookItem bookInfo = bookItemService.getBookItemById(param.getBookId());
        return getSuccessResult(bookInfo);
    }

    /**
     * 取消预约
     */
    @ApiOperation(value = "取消预约")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request, @RequestBody BookDetailParam param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer bookId = param.getBookId();
        if (bookId == null) {
            return getFailureResult(201, "预约ID不能为空");
        }

        MtBookItem bookItem = bookItemService.getBookItemById(bookId);
        if (bookItem == null) {
            return getFailureResult(201, "预约单已不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(bookItem.getStoreId()))) {
            return getFailureResult(1004);
        }

        Boolean result = bookItemService.cancelBook(bookItem.getId(), "店员取消");
        return getSuccessResult(result);
    }

    /**
     * 确定预约
     */
    @ApiOperation(value = "确定预约")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject confirm(HttpServletRequest request, @RequestBody BookDetailParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));

        Integer bookId = param.getBookId();
        MtBookItem bookItem = bookItemService.getBookItemById(bookId);
        if (bookItem == null) {
            return getFailureResult(201, "预约单不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());
        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(bookItem.getStoreId()))) {
            return getFailureResult(1004);
        }

        bookItemService.updateBookItem(bookItem);
        return getSuccessResult(true);
    }
}
