package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.ExcelUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.fuint.utils.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import static com.fuint.common.util.XlsUtil.objectConvertToString;

/**
 * 订单管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/order")
public class BackendOrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendOrderController.class);

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 后台账户服务接口
     */
    private AccountService accountService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 微信服务接口
     * */
    private WeixinService weixinService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 订单列表查询
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "订单列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account.getMerchantId() != null && account.getMerchantId() > 0) {
            orderListParam.setMerchantId(account.getMerchantId());
        }
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();
        if (storeId > 0) {
            orderListParam.setStoreId(storeId);
        }
        PaginationResponse response = orderService.getUserOrderList(orderListParam);
        // 订单类型列表
        List<ParamDto> typeList = OrderTypeEnum.getOrderTypeList();

        // 订单状态列表
        List<ParamDto> statusList = OrderStatusEnum.getOrderStatusList();

        // 支付状态列表
        List<ParamDto> payStatusList = PayStatusEnum.getPayStatusList();

        // 配送类型列表
        List<ParamDto> orderModeList = OrderModeEnum.getOrderModeList();

        // 店铺列表
        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            paramsStore.put("storeId", storeId.toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        // 支付方式列表
        List<ParamDto> payTypeList = PayTypeEnum.getPayTypeList();

        // 物流公司列表
        List<ParamDto> expressCompanyList = ExpressCompanyEnum.getExpressCompanyList();

        Map<String, Object> result = new HashMap<>();
        result.put("typeList", typeList);
        result.put("statusList", statusList);
        result.put("payStatusList", payStatusList);
        result.put("orderModeList", orderModeList);
        result.put("storeList", storeList);
        result.put("payTypeList", payTypeList);
        result.put("expressCompanyList", expressCompanyList);
        result.put("paginationResponse", response);

        return getSuccessResult(result);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return
     * */
    @ApiOperation(value = "获取订单详情")
    @RequestMapping(value = "/info/{orderId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:detail')")
    public ResponseObject info(@PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        // 支付方式列表
        List<ParamDto> payTypeList = PayTypeEnum.getPayTypeList();

        // 支付状态列表
        List<ParamDto> payStatusList = PayStatusEnum.getPayStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("payTypeList", payTypeList);
        result.put("payStatusList", payStatusList);

        return getSuccessResult(result);
    }

    /**
     * 确认发货
     * @param request HttpServletRequest对象
     * @return
     * */
    @ApiOperation(value = "确认发货")
    @RequestMapping(value = "/delivered", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:delivery')")
    public ResponseObject delivered(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String expressCompany = param.get("expressCompany") == null ? "" : param.get("expressCompany").toString();
        String expressNo = param.get("expressNo") == null ? "" : param.get("expressNo").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        MtUser userInfo = memberService.queryMemberById(orderInfo.getUserId());

        OrderDto dto = new OrderDto();
        dto.setId(orderId);
        dto.setStatus(OrderStatusEnum.DELIVERED.getKey());
        dto.setOperator(accountInfo.getAccountName());
        if (StringUtil.isNotEmpty(expressCompany) || StringUtil.isNotEmpty(expressNo)) {
            ExpressDto expressInfo = new ExpressDto();
            String time = TimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm");
            expressInfo.setExpressTime(time);
            expressInfo.setExpressNo(expressNo);
            expressInfo.setExpressCompany(expressCompany);
            ExpressCompanyEnum[] expressCompanyEnums = ExpressCompanyEnum.values();
            for (ExpressCompanyEnum expressCompanyEnum : expressCompanyEnums) {
                 if (expressCompanyEnum.getValue().equals(expressCompany)) {
                     expressInfo.setExpressCode(expressCompanyEnum.getKey());
                     break;
                 }
            }
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
            weixinService.sendSubscribeMessage(userInfo.getMerchantId(), userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.DELIVER_GOODS.getKey(), "pages/order/index", params, sendTime);
        }

        return getSuccessResult(true);
    }

    /**
     * 修改订单
     * @param request HttpServletRequest对象
     * @return
     * */
    @ApiOperation(value = "修改订单")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:edit')")
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String amount = param.get("amount") == null ? "" : param.get("amount").toString();
        String discount = param.get("discount") == null ? "" : param.get("discount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String orderMode = param.get("orderMode") == null ? "" : param.get("orderMode").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
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

        orderService.updateOrder(orderDto);
        return getSuccessResult(true);
    }

    /**
     * 验证并核销订单
     * @param request HttpServletRequest对象
     * @return
     * */
    @ApiOperation(value = "验证并核销订单")
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('cashier:confirmOrder')")
    public ResponseObject verify(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String verifyCode = param.get("verifyCode") == null ? "" : param.get("verifyCode").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOperator(accountInfo.getAccountName());
        if (StringUtil.isNotEmpty(remark)) {
            orderDto.setConfirmRemark(remark);
        }
        if (StringUtil.isNotEmpty(verifyCode)) {
            orderDto.setVerifyCode(verifyCode);
        }

        orderService.updateOrder(orderDto);
        return getSuccessResult(true);
    }

    /**
     * 最新订单列表查询
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "最新订单列表查询")
    @RequestMapping(value = "/latest", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject latest(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        Map<String, Object> result = new HashMap<>();
        if (accountInfo == null) {
            result.put("goodsList", new ArrayList<>());
            return getSuccessResult(result);
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();
        Integer staffId = account.getStaffId();
        if (storeId > 0) {
            orderListParam.setStoreId(storeId);
        }
        if (staffId > 0) {
            orderListParam.setStaffId(staffId.toString());
        }

        PaginationResponse response = orderService.getUserOrderList(orderListParam);
        return getSuccessResult(response);
    }

    /**
     * 删除订单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:delete')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String operator = accountInfo.getAccountName();
        orderService.deleteOrder(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 订单设置详情
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "订单设置详情")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:setting')")
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.ORDER.getKey());
        Map<String, Object> result = new HashMap();
        String deliveryFee = "";
        String isClose = "";
        String deliveryMinAmount = "";
        String mpUploadShipping = "";
        String payOffLine = "off";

        for (MtSetting setting : settingList) {
            if (setting.getName().equals(OrderSettingEnum.DELIVERY_FEE.getKey())) {
                deliveryFee = setting.getValue();
            } else if (setting.getName().equals(OrderSettingEnum.IS_CLOSE.getKey())) {
                isClose = setting.getValue();
            } else if (setting.getName().equals(OrderSettingEnum.DELIVERY_MIN_AMOUNT.getKey())) {
                deliveryMinAmount = setting.getValue();
            } else if (setting.getName().equals(OrderSettingEnum.MP_UPLOAD_SHIPPING.getKey())) {
                mpUploadShipping = setting.getValue();
            } else if (setting.getName().equals(OrderSettingEnum.PAY_OFF_LINE.getKey())) {
                payOffLine = setting.getValue();
            }
        }

        result.put("deliveryFee", deliveryFee);
        result.put("isClose", isClose);
        result.put("deliveryMinAmount", deliveryMinAmount);
        result.put("mpUploadShipping", mpUploadShipping);
        result.put("payOffLine", payOffLine);

        return getSuccessResult(result);
    }

    /**
     * 保存订单设置
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存订单设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:setting')")
    public ResponseObject saveSetting(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String deliveryFee = param.get("deliveryFee") != null ? param.get("deliveryFee").toString() : "0";
        String isClose = param.get("isClose") != null ? param.get("isClose").toString() : YesOrNoEnum.FALSE.getKey();
        String deliveryMinAmount = param.get("deliveryMinAmount") != null ? param.get("deliveryMinAmount").toString() : "0";
        String payOffLine = param.get("payOffLine") != null ? param.get("payOffLine").toString() : "off";

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo.getAccountName();

        OrderSettingEnum[] settingList = OrderSettingEnum.values();
        for (OrderSettingEnum setting : settingList) {
            MtSetting info = new MtSetting();
            info.setType(SettingTypeEnum.ORDER.getKey());
            info.setName(setting.getKey());
            if (setting.getKey().equals(OrderSettingEnum.DELIVERY_FEE.getKey())) {
                info.setValue(deliveryFee);
            } else if (setting.getKey().equals(OrderSettingEnum.IS_CLOSE.getKey())) {
                info.setValue(isClose);
            } else if (setting.getKey().equals(OrderSettingEnum.DELIVERY_MIN_AMOUNT.getKey())) {
                info.setValue(deliveryMinAmount);
            } else if (setting.getKey().equals(OrderSettingEnum.PAY_OFF_LINE.getKey())) {
                info.setValue(payOffLine);
            }
            info.setMerchantId(accountInfo.getMerchantId());
            info.setStoreId(accountInfo.getStoreId());
            info.setDescription(setting.getValue());
            info.setStatus(StatusEnum.ENABLED.getKey());
            info.setOperator(operator);
            info.setUpdateTime(new Date());

            settingService.saveSetting(info);
        }

        return getSuccessResult(true);
    }

    /**
     * 导出订单
     *
     * @return
     */
    @ApiOperation(value = "导出订单")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getParameter("token");
        String storeId = request.getParameter("storeId") == null ? "" : request.getParameter("storeId");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String payStatus = request.getParameter("payStatus") == null ? "" : request.getParameter("payStatus");
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        OrderListParam params = new OrderListParam();
        params.setPage(1);
        params.setPageSize(Constants.MAX_ROWS);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.setMerchantId(accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(storeId)) {
            params.setStoreId(Integer.parseInt(storeId));
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.setUserId(userId);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.setMobile(mobile);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.setStatus(status);
        }
        if (StringUtil.isNotEmpty(payStatus)) {
            params.setPayStatus(payStatus);
        }
        if (StringUtil.isNotEmpty(startTime)) {
            params.setStartTime(startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.setEndTime(endTime);
        }

        PaginationResponse<UserOrderDto> result = orderService.getUserOrderList(params);

        // excel标题
        String[] title = { "订单号", "会员名称", "订单类型", "所属门店", "总金额", "支付状态", "订单状态" };

        // excel文件名
        String fileName = "订单列表"+ DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") +".xls";

        // sheet名
        String sheetName = "订单列表";

        String[][] content = null;

        List<UserOrderDto> list = result.getContent();

        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            UserOrderDto orderDto = list.get(i);
            if (orderDto != null) {
                String storeName = "";
                String userName = "";
                if (orderDto.getStoreInfo() != null) {
                    storeName = orderDto.getStoreInfo().getName();
                }
                if (orderDto.getUserInfo() != null) {
                    userName = orderDto.getUserInfo().getName();
                }
                content[i][0] = objectConvertToString(orderDto.getOrderSn());
                content[i][1] = objectConvertToString(userName);
                content[i][2] = objectConvertToString(orderDto.getTypeName());
                content[i][3] = objectConvertToString(storeName);
                content[i][4] = objectConvertToString(orderDto.getAmount());
                content[i][5] = objectConvertToString(orderDto.getPayStatus());
                content[i][6] = objectConvertToString(orderDto.getStatusText());
            }
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        ExcelUtil.setResponseHeader(response, fileName, wb);

        logger.info("导出订单成功...");
    }
}
