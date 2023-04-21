package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SendLogService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtSendLog;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡券发放记录管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-卡券发放相关接口")
@RestController
@RequestMapping(value = "/backendApi/sendLog")
public class BackendSendLogController extends BaseController {

    /**
     * 发送记录服务接口
     */
    @Autowired
    private SendLogService sendLogService;

    /**
     * 会员接口服务
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 卡券服务接口
     * */
    @Autowired
    CouponService couponService;

    /**
     * 查询发券记录列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String couponId = request.getParameter("couponId") == null ? "" : request.getParameter("couponId");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }

        if (StringUtil.isNotEmpty(userId)) {
            searchParams.put("userId", userId);
        }

        if (StringUtil.isNotEmpty(couponId)) {
            searchParams.put("couponId", couponId);
        }

        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            if (userInfo != null) {
                searchParams.put("userId", userInfo.getId().toString());
            } else {
                searchParams.put("userId", "0");
            }
        }

        paginationRequest.setSortColumn(new String[]{"updateTime desc", "id desc"});
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 废除用户卡券
     *
     * @param request
     * @param id 日志ID
     * @return
     */
    @RequestMapping(value = "/removeUserCoupon/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject removeUserCoupon(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (id == null) {
            return getFailureResult(201, "系统参数有误");
        }

        MtSendLog sendLog = sendLogService.querySendLogById(id);
        if (sendLog == null) {
            return getFailureResult(201, "系统参数有误");
        }

        try {
            couponService.removeUserCoupon(id, sendLog.getUuid(), accountInfo.getAccountName());
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }
}
