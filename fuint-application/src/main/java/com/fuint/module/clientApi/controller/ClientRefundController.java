package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.RefundDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.RefundStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.RefundService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.RefundListRequest;
import com.fuint.module.clientApi.request.RefundSubmitRequest;
import com.fuint.repository.model.MtRefund;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-售后相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/refund")
public class ClientRefundController extends BaseController {

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 获取售后订单列表
     */
    @ApiOperation(value = "获取售后订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(@ModelAttribute RefundListRequest param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        param.setUserId(userInfo.getId());
        String status = param.getStatus() != null ? param.getStatus() : "";
        if (status.equals("1")) {
            status = RefundStatusEnum.CREATED.getKey();
        } else {
            status = "";
        }
        Map<String, Object> params = new HashMap();
        params.put("userId", userInfo.getId());
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        params.put("pageNumber", param.getPage());

        ResponseObject orderData = refundService.getUserRefundList(params);
        return getSuccessResult(orderData.getData());
    }

    /**
     * 售后订单提交
     */
    @ApiOperation(value = "售后订单提交")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(@RequestBody RefundSubmitRequest param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        if (null == mtUser) {
            return getFailureResult(1001);
        }
        param.setUserId(mtUser.getId());

        Integer orderId = param.getOrderId() == null ? 0 : param.getOrderId();
        String remark = param.getRemark() == null ? "" : param.getRemark();
        String type = param.getType() == null ? "" : param.getType();
        List<String> images = param.getImages() == null ? new ArrayList<>() : param.getImages();

        UserOrderDto order = orderService.getOrderById(orderId);
        if (order == null || (!order.getUserId().equals(mtUser.getId()))) {
            return getFailureResult(2001);
        }

        RefundDto refundDto = new RefundDto();
        refundDto.setUserId(mtUser.getId());
        refundDto.setOrderId(order.getId());
        refundDto.setRemark(remark);
        refundDto.setType(type);
        if (order.getStoreInfo() != null) {
            refundDto.setStoreId(order.getStoreInfo().getId());
        }
        refundDto.setMerchantId(order.getMerchantId());
        if (order.getStoreInfo() != null) {
            refundDto.setStoreId(order.getStoreInfo().getId());
        }
        refundDto.setAmount(order.getPayAmount());
        if (images.size() > 0) {
            refundDto.setImages(String.join(",", images));
        }
        MtRefund refundInfo = refundService.createRefund(refundDto);

        Map<String, Object> outParams = new HashMap();
        outParams.put("refundInfo", refundInfo);

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 获取售后订单详情
     */
    @ApiOperation(value = "获取售后订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String refundId = request.getParameter("refundId");
        if (StringUtil.isEmpty(refundId)) {
            return getFailureResult(2000, "售后订单ID不能为空");
        }
        RefundDto refundInfo = refundService.getRefundById(Integer.parseInt(refundId));
        return getSuccessResult(refundInfo);
    }

    /**
     * 售后用户发货
     */
    @ApiOperation(value = "售后用户发货")
    @RequestMapping(value = "/delivery", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject delivery(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        param.put("userId", mtUser.getId());
        String refundId = param.get("refundId") == null ? "" : param.get("refundId").toString();
        String expressName = param.get("expressName") == null ? "" : param.get("expressName").toString();
        String expressNo = param.get("expressNo") == null ? "" : param.get("expressNo").toString();

        RefundDto refundInfo = refundService.getRefundById(Integer.parseInt(refundId));
        if (refundInfo == null || (!refundInfo.getUserId().equals(mtUser.getId()))) {
            return getFailureResult(2001);
        }

        if (StringUtil.isEmpty(expressName) || StringUtil.isEmpty(expressNo)) {
            return getFailureResult(201, "物流信息不能为空");
        }

        RefundDto refundDto = new RefundDto();
        refundDto.setId(Integer.parseInt(refundId));
        refundDto.setExpressName(expressName);
        refundDto.setExpressNo(expressNo);
        refundService.updateRefund(refundDto);

        return getSuccessResult(true);
    }
}
