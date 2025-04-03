package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ConfirmLogDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.ConfirmLogService;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.TAccount;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员卡券核销流水
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-券核销流水相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/confirmLog")
public class BackendConfirmLogController extends BaseController {

    /**
     * 卡券核销流水接口
     */
    private ConfirmLogService confirmLogService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 会员接口服务
     * */
    private MemberService memberService;

    /**
     * 后台账户服务接口
     */
    private AccountService tAccountService;

    /**
     * 获取会员卡券核销记录列表
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "获取会员卡券核销记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:confirmLog:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String couponId = request.getParameter("couponId") == null ? "" : request.getParameter("couponId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }
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
            MtUser userInfo = memberService.queryMemberByMobile(accountInfo.getMerchantId(), mobile);
            if (userInfo != null) {
                searchParams.put("userId", userInfo.getId().toString());
            } else {
                searchParams.put("userId", "0");
            }
        }
        paginationRequest.setSearchParams(searchParams);

        // 登录员工所属店铺处理
        TAccount tAccount = tAccountService.getAccountInfoById(accountInfo.getId());
        if (tAccount.getStoreId() > 0 && tAccount.getStoreId() > 0) {
            searchParams.put("storeId", tAccount.getStoreId());
        }

        PaginationResponse<ConfirmLogDto> paginationResponse = confirmLogService.queryConfirmLogListByPagination(paginationRequest);

        // 卡券类型列表
        List<ParamDto> typeList = CouponTypeEnum.getCouponTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 撤销已使用的卡券
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "撤销已使用的卡券")
    @RequestMapping(value = "/rollbackUserCoupon/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:confirmLog:index')")
    public ResponseObject rollbackUserCoupon(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = (request.getParameter("userCouponId") == null || StringUtil.isEmpty(request.getParameter("userCouponId"))) ? "0" : request.getParameter("userCouponId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        couponService.rollbackUserCoupon(id, Integer.parseInt(userCouponId), accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
