package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.RefundDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.RefundStatusEnum;
import com.fuint.common.enums.RefundTypeEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.RefundService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
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
 * 售后管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-售后订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/refund")
public class BackendRefundController extends BaseController {

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 会员接口服务
     * */
    private MemberService memberService;

    /**
     * 售后列表查询
     */
    @ApiOperation(value = "售后列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String orderSn = request.getParameter("orderSn");
        String mobile = request.getParameter("mobile");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
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
            MtUser userInfo = memberService.queryMemberByMobile(accountInfo.getMerchantId(), mobile);
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
        if (StringUtil.isNotEmpty(startTime)) {
            params.put("startTime", startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        PaginationResponse<RefundDto> paginationResponse = refundService.getRefundListByPagination(new PaginationRequest(page, pageSize, params));

        // 售后状态列表
        List<ParamDto> statusList = RefundStatusEnum.getRefundStatusList();

        // 售后类型列表
        List<ParamDto> refundTypeList = RefundTypeEnum.getRefundTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("statusList", statusList);
        result.put("refundTypeList", refundTypeList);

        return getSuccessResult(result);
    }

    /**
     * 查询售后详情
     * */
    @ApiOperation(value = "查询售后详情")
    @RequestMapping(value = "/info/{refundId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject info(@PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        RefundDto refundInfo = refundService.getRefundById(refundId);
        UserOrderDto orderInfo = null;
        if (refundInfo != null) {
            orderInfo = orderService.getOrderById(refundInfo.getOrderId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(refundInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("refundInfo", refundInfo);
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存售后订单
     */
    @ApiOperation(value = "保存售后订单")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject save(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer refundId = param.get("refundId") == null ? 0 : Integer.parseInt(param.get("refundId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String rejectReason = param.get("rejectReason") == null ? "" : param.get("rejectReason").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (status.equals(RefundStatusEnum.REJECT.getKey())) {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(RefundStatusEnum.REJECT.getKey());
            dto.setRemark(remark);
            dto.setRejectReason(rejectReason);
            refundService.updateRefund(dto);
        } else {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(status);
            dto.setRemark(remark);
            if (status.equals(RefundStatusEnum.COMPLETE.getKey())) {
                refundService.agreeRefund(dto);
            } else {
                refundService.updateRefund(dto);
            }
        }
        return getSuccessResult(true);
    }

    /**
     * 发起退款
     */
    @ApiOperation(value = "发起退款")
    @RequestMapping(value = "doRefund", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject doRefund(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String refundAmount = param.get("refundAmount") == null ? "" : param.get("refundAmount").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (orderId <= 0 || StringUtil.isEmpty(refundAmount)) {
            return getFailureResult(201, "参数有误，发起退款失败");
        }
        Boolean result = refundService.doRefund(orderId, refundAmount, remark, accountInfo);
        if (result) {
            return getSuccessResult(true);
        } else {
            return getFailureResult(201, "退款失败");
        }
    }
}
