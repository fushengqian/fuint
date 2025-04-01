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
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/order")
public class MerchantOrderController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "获取订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());

        if (staffInfo == null) {
            return getFailureResult(1004);
        } else {
            params.setMerchantId(staffInfo.getMerchantId());
            params.setStoreId(staffInfo.getStoreId());
        }

        PaginationResponse orderData = orderService.getUserOrderList(params);
        return getSuccessResult(orderData);
    }

    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody OrderDetailParam param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff mtStaff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (mtStaff == null) {
            return getFailureResult(1004);
        }

        String orderId = param.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getMyOrderById(Integer.parseInt(param.getOrderId()));

        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request, @RequestBody OrderDetailParam param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        String orderId = param.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "订单不能为空");
        }

        UserOrderDto orderDto = orderService.getOrderById(Integer.parseInt(orderId));
        if (orderDto == null) {
            return getFailureResult(201, "订单已不存在");
        }

        MtStaff staffInfo = staffService.queryStaffByUserId(mtUser.getId());
        if (staffInfo == null || orderDto.getStoreInfo() == null || !staffInfo.getStoreId().equals(orderDto.getStoreInfo().getId())) {
            return getFailureResult(201, "没有操作权限");
        }

        MtOrder orderInfo = orderService.cancelOrder(orderDto.getId(), "店员取消");
        return getSuccessResult(orderInfo);
    }
}
