package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.OrderDto;
import com.fuint.common.dto.order.UserOrderDto;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.QRCodeUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/order")
public class ClientOrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientOrderController.class);

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 获取我的订单列表
     */
    @ApiOperation(value = "获取我的订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        orderListParam.setUserId(userInfo.getId());
        PaginationResponse orderData = orderService.getUserOrderList(orderListParam);
        return getSuccessResult(orderData);
    }

    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }
        UserOrderDto orderInfo;
        if (orderId.length() >= 12) {
            orderInfo = orderService.getOrderByOrderSn(orderId);
        } else {
            orderInfo = orderService.getMyOrderById(Integer.parseInt(orderId));
        }
        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "订单不能为空");
        }

        if (orderId.length() >= 12) {
            MtOrder mtOrder = orderService.getOrderInfoByOrderSn(orderId);
            if (mtOrder != null) {
                orderId = mtOrder.getId().toString();
            }
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(201, "订单信息有误");
        }

        MtOrder orderInfo = orderService.cancelOrder(order.getId(), "会员取消");
        return getSuccessResult(orderInfo);
    }

    /**
     * 确认收货
     */
    @ApiOperation(value = "确认收货")
    @RequestMapping(value = "/receipt", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject receipt(HttpServletRequest request) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "订单信息有误");
        }

        OrderDto reqDto = new OrderDto();
        reqDto.setId(Integer.parseInt(orderId));
        reqDto.setStatus(OrderStatusEnum.RECEIVED.getKey());
        MtOrder orderInfo = orderService.updateOrder(reqDto);

        return getSuccessResult(orderInfo);
    }

    /**
     * 获取待办订单数量
     */
    @ApiOperation(value = "获取待办订单数量")
    @RequestMapping(value = "/todoCounts", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject todoCounts() {
        UserInfo userInfo = TokenUtil.getUserInfo();

        Map<String, Object> result = new HashMap<>();
        if (userInfo != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("status", OrderStatusEnum.CREATED.getKey());
            param.put("user_id", userInfo.getId());
            List<MtOrder> data = orderService.getOrderListByParams(param);
            result.put("toPay", data.size());
        }

        return getSuccessResult(result);
    }

    /**
     * 生成订单核销二维码
     */
    @ApiOperation(value = "生成订单核销二维码")
    @RequestMapping(value = "/verifyQrCode", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject verifyQrCode(HttpServletRequest request) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo;
        if (orderId.length() >= 12) {
            orderInfo = orderService.getOrderByOrderSn(orderId);
        } else {
            orderInfo = orderService.getMyOrderById(Integer.parseInt(orderId));
        }

        if (!orderInfo.getUserId().equals(mtUser.getId())) {
            return getFailureResult(201, "订单信息有误");
        }

        if (orderInfo.getVerifyCode() == null || StringUtil.isEmpty(orderInfo.getVerifyCode())) {
            return getFailureResult(201, "该订单无需核销");
        }

        // 构建二维码内容：JSON格式包含订单ID和核销码
        String qrContent = "{\"orderId\":" + orderInfo.getId() + ",\"code\":\"" + orderInfo.getVerifyCode() + "\"}";

        // 生成二维码图片并编码为base64
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(outputStream, qrContent, 400, 400, "png", null);
            String base64QrCode = "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
            outputStream.close();

            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", base64QrCode);
            result.put("verifyCode", orderInfo.getVerifyCode());
            return getSuccessResult(result);
        } catch (Exception e) {
            logger.error("生成核销二维码失败：{}", e.getMessage());
            return getFailureResult(201, "生成核销二维码失败");
        }
    }
}
