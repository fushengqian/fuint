package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.param.OrderDetailParam;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.param.RefundListParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.RefundService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 售后类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-售后管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/refund")
public class MerchantRefundController extends BaseController {

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
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 获取售后订单列表
     */
    @ApiOperation(value = "获取售订单后列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody RefundListParam params) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null) {
            return getFailureResult(1001);
        } else {
            params.setMerchantId(staffInfo.getMerchantId());
            params.setStoreId(staffInfo.getStoreId());
        }

        Map<String, Object> param = new HashMap<>();
        PaginationResponse paginationResponse = refundService.getRefundListByPagination(new PaginationRequest(params.getPage(), params.getPageSize(), param));
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取售后订单详情
     */
    @ApiOperation(value = "获取售后订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody OrderDetailParam param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));

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
     * 更新售后订单
     */
    @ApiOperation(value = "更新售后订单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(HttpServletRequest request, @RequestBody OrderDetailParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));

        String orderId = param.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "订单不能为空");
        }

        UserOrderDto orderDto = orderService.getOrderById(Integer.parseInt(orderId));
        if (orderDto == null) {
            return getFailureResult(201, "订单已不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(orderDto.getStoreInfo().getId()))) {
            return getFailureResult(1004);
        }

        MtOrder orderInfo = orderService.cancelOrder(orderDto.getId(), "店员取消");
        return getSuccessResult(orderInfo);
    }
}
