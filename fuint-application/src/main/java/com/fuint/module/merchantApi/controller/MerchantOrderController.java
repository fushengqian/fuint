package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.param.OrderDetailParam;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 订单类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-订单管理相关接口")
@RestController
@RequestMapping(value = "/merchantApi/order")
public class MerchantOrderController extends BaseController {

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    @Autowired
    private StaffService staffService;

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "获取订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = null;
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(1002, "该账号不是商户");
        } else if(staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0){
            orderListParam.setStoreId(staffInfo.getStoreId().toString());
        }

        ResponseObject orderData = orderService.getUserOrderList(orderListParam);
        return getSuccessResult(orderData.getData());
    }

    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody OrderDetailParam orderDetailParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = orderDetailParam.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getMyOrderById(Integer.parseInt(orderDetailParam.getOrderId()));

        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request, @RequestBody OrderDetailParam orderDetailParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = orderDetailParam.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));

        MtOrder orderInfo = orderService.cancelOrder(order.getId(), "店员取消");

        return getSuccessResult(orderInfo);
    }
}
