package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping(value = "/clientApi/order")
public class ClientOrderController extends BaseController {

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 获取我的订单列表
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
        ResponseObject orderData = orderService.getUserOrderList(param);

        return getSuccessResult(orderData.getData());
    }

    /**
     * 获取订单详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getMyOrderById(Integer.parseInt(orderId));

        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "订单信息有误");
        }

        MtOrder orderInfo = orderService.cancelOrder(order.getId(), "会员取消");

        return getSuccessResult(orderInfo);
    }

    /**
     * 确认收货
     */
    @RequestMapping(value = "/receipt", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject receipt(HttpServletRequest request) throws BusinessCheckException{
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

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
     * 获取待办订单
     */
    @RequestMapping(value = "/todoCounts", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject todoCounts(HttpServletRequest request) throws BusinessCheckException{
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

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
}
