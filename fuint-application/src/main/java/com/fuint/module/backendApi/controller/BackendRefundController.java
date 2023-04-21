package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.RefundStatusEnum;
import com.fuint.common.enums.RefundTypeEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.RefundService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.TAccount;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-售后订单相关接口")
@RestController
@RequestMapping(value = "/backendApi/refund")
public class BackendRefundController extends BaseController {

    /**
     * 售后服务接口
     * */
    @Autowired
    private RefundService refundService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private AccountService accountService;

    /**
     * 会员接口服务
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 退款列表查询
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String orderSn = request.getParameter("orderSn");
        String mobile = request.getParameter("mobile");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(orderSn)) {
            UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
            if (orderInfo != null) {
                params.put("orderId", orderInfo.getId().toString());
            } else {
                params.put("orderId", "0");
            }
        }
        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            if (userInfo != null) {
                userId = userInfo.getId().toString();
            } else {
                userId = "0";
            }
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.put("userId", userId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtRefund> paginationResponse = refundService.getRefundListByPagination(paginationRequest);

        // 售后状态列表
        RefundStatusEnum[] statusListEnum = RefundStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (RefundStatusEnum enumItem : statusListEnum) {
             ParamDto paramDto = new ParamDto();
             paramDto.setKey(enumItem.getKey());
             paramDto.setName(enumItem.getValue());
             paramDto.setValue(enumItem.getKey());
             statusList.add(paramDto);
        }

        // 售后类型列表
        RefundTypeEnum[] refundTypeEnums = RefundTypeEnum.values();
        List<ParamDto> refundTypeList = new ArrayList<>();
        for (RefundTypeEnum enumItem : refundTypeEnums) {
             ParamDto paramDto = new ParamDto();
             paramDto.setKey(enumItem.getKey());
             paramDto.setName(enumItem.getValue());
             paramDto.setValue(enumItem.getKey());
             refundTypeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("statusList", statusList);
        result.put("refundTypeList", refundTypeList);

        return getSuccessResult(result);
    }

    /**
     * 退款详情
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/info/{refundId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        RefundDto refundInfo = refundService.getRefundById(refundId);
        UserOrderDto orderInfo = null;
        if (refundInfo != null) {
            orderInfo = orderService.getOrderById(refundInfo.getOrderId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("refundInfo", refundInfo);
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存处理售后订单
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer refundId = param.get("refundId") == null ? 0 : Integer.parseInt(param.get("refundId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();

        if (status.equals(RefundStatusEnum.REJECT.getKey())) {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(operator);
            dto.setStatus(RefundStatusEnum.REJECT.getKey());
            dto.setRemark(remark);
            refundService.updateRefund(dto);
        } else {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(operator);
            dto.setStatus(RefundStatusEnum.APPROVED.getKey());
            dto.setRemark(remark);
            refundService.agreeRefund(dto);
        }

        return getSuccessResult(true);
    }
}
