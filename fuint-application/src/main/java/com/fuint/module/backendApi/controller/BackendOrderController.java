package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.TAccount;
import com.fuint.utils.StringUtil;
import com.fuint.utils.TimeUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 订单管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-订单相关接口")
@RestController
@RequestMapping(value = "/backendApi/order")
public class BackendOrderController extends BaseController {

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
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 订单列表查询
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String type = request.getParameter("type");
        String orderSn = request.getParameter("orderSn");
        String status = request.getParameter("status");
        String payStatus = request.getParameter("payStatus");
        String userId = request.getParameter("userId");
        String mobile = request.getParameter("mobile");
        String storeIdStr = request.getParameter("storeId");
        String orderMode = request.getParameter("orderMode");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);
        if (StringUtil.isNotEmpty(type)) {
            param.put("type", type);
        }
        if (StringUtil.isNotEmpty(orderSn)) {
            param.put("orderSn", orderSn);
        }
        if (StringUtil.isNotEmpty(status)) {
            param.put("status", status);
        }
        if (StringUtil.isNotEmpty(payStatus)) {
            param.put("payStatus", payStatus);
        }
        if (StringUtil.isNotEmpty(userId)) {
            param.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            param.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(storeIdStr)) {
            param.put("storeId", storeIdStr);
        }
        if (StringUtil.isNotEmpty(orderMode)) {
            param.put("orderMode", orderMode);
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            param.put("storeId", storeId);
        }

        ResponseObject response = orderService.getUserOrderList(param);

        // 订单类型列表
        OrderTypeEnum[] typeListEnum = OrderTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (OrderTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        // 订单状态列表
        OrderStatusEnum[] statusListEnum = OrderStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (OrderStatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        // 支付状态列表
        PayStatusEnum[] payStatusListEnum = PayStatusEnum.values();
        List<ParamDto> payStatusList = new ArrayList<>();
        for (PayStatusEnum enumItem : payStatusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            payStatusList.add(paramDto);
        }

        // 配送类型列表
        OrderModeEnum[] orderModeEnums = OrderModeEnum.values();
        List<ParamDto> orderModeList = new ArrayList<>();
        for (OrderModeEnum enumItem : orderModeEnums) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            orderModeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("typeList", typeList);
        result.put("statusList", statusList);
        result.put("payStatusList", payStatusList);
        result.put("orderModeList", orderModeList);
        result.put("paginationResponse", response.getData());

        return getSuccessResult(result);
    }

    /**
     * 订单详情
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/info/{orderId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        UserOrderDto orderInfo = orderService.getOrderById(orderId);

        // 支付方式列表
        PayTypeEnum[] payTypes = PayTypeEnum.values();
        List<ParamDto> payTypeList = new ArrayList<>();
        for (PayTypeEnum catchTypeEnum : payTypes) {
            ParamDto catchType = new ParamDto();
            catchType.setKey(catchTypeEnum.getKey());
            catchType.setName(catchTypeEnum.getValue());
            catchType.setValue(catchTypeEnum.getKey());
            payTypeList.add(catchType);
        }

        // 支付状态列表
        PayStatusEnum[] payStatusListEnum = PayStatusEnum.values();
        List<ParamDto> payStatusList = new ArrayList<>();
        for (PayStatusEnum enumItem : payStatusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            payStatusList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("payTypeList", payTypeList);
        result.put("payStatusList", payStatusList);

        return getSuccessResult(result);
    }

    /**
     * 确认发货
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/delivered", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject delivered(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String expressCompany = param.get("expressCompany") == null ? "" : param.get("expressCompany").toString();
        String expressNo = param.get("expressNo") == null ? "" : param.get("expressNo").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        MtUser userInfo = memberService.queryMemberById(orderInfo.getUserId());

        OrderDto dto = new OrderDto();
        dto.setId(orderId);
        dto.setStatus(OrderStatusEnum.DELIVERED.getKey());

        if (StringUtil.isNotEmpty(expressCompany) || StringUtil.isNotEmpty(expressNo)) {
            ExpressDto expressInfo = new ExpressDto();
            String time = TimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm");
            expressInfo.setExpressTime(time);
            expressInfo.setExpressNo(expressNo);
            expressInfo.setExpressCompany(expressCompany);
            dto.setExpressInfo(expressInfo);
        }

        orderService.updateOrder(dto);

        // 发送小程序订阅消息
        if (orderInfo != null && userInfo != null && orderInfo.getAddress() != null) {
            Date nowTime = new Date();
            Date sendTime = new Date(nowTime.getTime() - 60000);
            Map<String, Object> params = new HashMap<>();
            params.put("receiver", orderInfo.getAddress().getName());
            params.put("orderSn", orderInfo.getOrderSn());
            params.put("expressCompany", expressCompany);
            params.put("expressNo", expressNo);
            weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.DELIVER_GOODS.getKey(), "pages/order/index", params, sendTime);
        }

        return getSuccessResult(true);
    }

    /**
     * 修改订单
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String amount = param.get("amount") == null ? "" : param.get("amount").toString();
        String discount = param.get("discount") == null ? "" : param.get("discount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String orderMode = param.get("orderMode") == null ? "" : param.get("orderMode").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOperator(accountInfo.getAccountName());
        if (StringUtil.isNotEmpty(status)) {
            orderDto.setStatus(status);
        }
        if (StringUtil.isNotEmpty(amount)) {
            orderDto.setAmount(new BigDecimal(amount));
        }
        if (StringUtil.isNotEmpty(discount)) {
            orderDto.setDiscount(new BigDecimal(discount));
        }
        if (StringUtil.isNotEmpty(remark)) {
            orderDto.setRemark(remark);
        }
        if (StringUtil.isNotEmpty(orderMode)) {
            orderDto.setOrderMode(orderMode);
        }

        try {
            orderService.updateOrder(orderDto);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 验证并核销订单
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject verify(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String verifyCode = param.get("verifyCode") == null ? "" : param.get("verifyCode").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOperator(accountInfo.getAccountName());
        if (StringUtil.isNotEmpty(remark)) {
            orderDto.setRemark(remark);
        }
        if (StringUtil.isNotEmpty(verifyCode)) {
            orderDto.setVerifyCode(verifyCode);
        }

        try {
            orderService.updateOrder(orderDto);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 最新订单列表查询
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject latest(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> result = new HashMap<>();
        if (accountInfo == null) {
            result.put("goodsList", new ArrayList<>());
            return getSuccessResult(result);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        Integer staffId = account.getStaffId();
        if (storeId > 0) {
            param.put("storeId", storeId.toString());
        }
        if (staffId > 0) {
            param.put("staffId", staffId.toString());
        }

        ResponseObject response = orderService.getUserOrderList(param);

        return getSuccessResult(response.getData());
    }

    /**
     * 删除订单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        orderService.deleteOrder(id, operator);

        return getSuccessResult(true);
    }
}
