package com.fuint.module.backendApi.controller.order;

import com.fuint.common.dto.order.RefundDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.dto.common.ParamDto;
import com.fuint.common.dto.order.UserOrderDto;
import com.fuint.common.enums.RefundStatusEnum;
import com.fuint.common.enums.RefundTypeEnum;
import com.fuint.common.param.RefundPage;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.RefundService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     * 售后列表查询
     */
    @ApiOperation(value = "售后列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject list(@ModelAttribute RefundPage refundPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            refundPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            refundPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<RefundDto> paginationResponse = refundService.getRefundListByPagination(refundPage);

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
        RefundDto refundDto = refundService.getRefundById(refundId);
        if (!refundDto.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        if (status.equals(RefundStatusEnum.REJECT.getKey())) {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(RefundStatusEnum.REJECT.getKey());
            dto.setRemark(remark);
            dto.setRejectReason(rejectReason);
            refundService.updateRefund(dto, accountInfo);
        } else {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(status);
            dto.setRemark(remark);
            if (status.equals(RefundStatusEnum.COMPLETE.getKey())) {
                refundService.agreeRefund(dto, accountInfo);
            } else {
                refundService.updateRefund(dto, accountInfo);
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
        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        if (!orderInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }
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
