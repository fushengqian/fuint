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
import com.fuint.repository.model.MtRefund;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 售后类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-售后相关接口")
@RestController
@RequestMapping(value = "/clientApi/refund")
public class ClientRefundController extends BaseController {

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
     * 获取售后订单列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        param.put("userId", userInfo.getId());

        String status = param.get("status") != null ? param.get("status").toString() : "";
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
        params.put("pageNumber", param.get("page").toString());

        ResponseObject orderData = refundService.getUserRefundList(params);
        return getSuccessResult(orderData.getData());
    }

    /**
     * 售后订单提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }
        param.put("userId", mtUser.getId());

        String orderId = param.get("orderId") == null ? "" : param.get("orderId").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String type = param.get("type") == null ? "" : param.get("type").toString();
        String images = param.get("images") == null ? "" : param.get("images").toString();

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
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
        refundDto.setAmount(order.getPayAmount());
        refundDto.setImages(images);
        MtRefund refundInfo = refundService.createRefund(refundDto);

        Map<String, Object> outParams = new HashMap();
        outParams.put("refundInfo", refundInfo);

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 获取售后订单详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String refundId = request.getParameter("refundId");
        if (StringUtil.isEmpty(refundId)) {
            return getFailureResult(2000, "售后订单ID不能为空");
        }

        RefundDto refundInfo = refundService.getRefundById(Integer.parseInt(refundId));

        return getSuccessResult(refundInfo);
    }
}
