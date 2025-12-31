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
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
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
import java.util.*;

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
     **/
    @ApiOperation(value = "订单列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public ResponseObject list(@RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            orderListParam.setMerchantId(accountInfo.getMerchantId());
        }
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
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
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

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
     * */
    @ApiOperation(value = "确认发货")
    @RequestMapping(value = "/delivered", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:delivery')")
    public ResponseObject delivered(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String expressCompany = param.get("expressCompany") == null ? "" : param.get("expressCompany").toString();
        String expressNo = param.get("expressNo") == null ? "" : param.get("expressNo").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
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
            Map<String, Object> params = new HashMap<>();
            params.put("receiver", orderInfo.getAddress().getName());
            params.put("orderSn", orderInfo.getOrderSn());
            params.put("expressCompany", expressCompany);
            params.put("expressNo", expressNo);
            weixinService.sendSubscribeMessage(userInfo.getMerchantId(), userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.DELIVER_GOODS.getKey(), "pages/order/index", params, nowTime);
        }

        return getSuccessResult(true);
    }

    /**
     * 修改订单
     * */
    @ApiOperation(value = "修改订单")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:edit')")
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String amount = param.get("amount") == null ? "" : param.get("amount").toString();
        String discount = param.get("discount") == null ? "" : param.get("discount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String orderMode = param.get("orderMode") == null ? "" : param.get("orderMode").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (orderId < 0) {
            return getFailureResult(201, "系统出错啦，订单ID不能为空");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOperator(accountInfo.getAccountName());
        if (StringUtil.isNotEmpty(status)) {
            orderDto.setStatus(status);
            if (status.equals(OrderStatusEnum.PAID.getKey())) {
                orderDto.setPayType(PayTypeEnum.CASH.getKey());
                orderDto.setRemark("后台修改订单状态为已支付");
            }
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
     * */
    @ApiOperation(value = "验证并核销订单")
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:edit')")
    public ResponseObject verify(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String verifyCode = param.get("verifyCode") == null ? "" : param.get("verifyCode").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
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
     */
    @ApiOperation(value = "最新订单列表查询")
    @RequestMapping(value = "/latest", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject latest(@RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Map<String, Object> result = new HashMap<>();
        if (accountInfo == null) {
            result.put("goodsList", new ArrayList<>());
            return getSuccessResult(result);
        }

        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        Integer staffId = accountInfo.getStaffId();
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
     */
    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:delete')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        orderService.deleteOrder(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 订单设置详情
     */
    @ApiOperation(value = "订单设置详情")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:setting')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.ORDER.getKey());
        Map<String, Object> result = new HashMap();
        String deliveryFee = "";
        String isClose = "";
        String deliveryMinAmount = "";
        String mpUploadShipping = "";
        String payOffLine = "off";
        String deliveryRange = "0";

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
            } else if (setting.getName().equals(OrderSettingEnum.DELIVERY_RANGE.getKey())) {
                deliveryRange = setting.getValue();
            }
        }

        result.put("deliveryFee", deliveryFee);
        result.put("isClose", isClose);
        result.put("deliveryMinAmount", deliveryMinAmount);
        result.put("mpUploadShipping", mpUploadShipping);
        result.put("payOffLine", payOffLine);
        result.put("deliveryRange", deliveryRange);

        return getSuccessResult(result);
    }

    /**
     * 保存交易设置
     */
    @ApiOperation(value = "保存交易设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:setting')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String deliveryFee = param.get("deliveryFee") != null ? param.get("deliveryFee").toString() : "0";
        String isClose = param.get("isClose") != null ? param.get("isClose").toString() : YesOrNoEnum.FALSE.getKey();
        String deliveryMinAmount = param.get("deliveryMinAmount") != null ? param.get("deliveryMinAmount").toString() : "0";
        String payOffLine = param.get("payOffLine") != null ? param.get("payOffLine").toString() : "off";
        String deliveryRange = param.get("deliveryRange") != null ? param.get("deliveryRange").toString() : "";

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
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
            } else if (setting.getKey().equals(OrderSettingEnum.DELIVERY_RANGE.getKey())) {
                info.setValue(deliveryRange);
            }
            info.setMerchantId(accountInfo.getMerchantId());
            info.setStoreId(accountInfo.getStoreId());
            info.setDescription(setting.getValue());
            info.setStatus(StatusEnum.ENABLED.getKey());
            info.setOperator(accountInfo.getAccountName());
            info.setUpdateTime(new Date());

            settingService.saveSetting(info);
        }

        return getSuccessResult(true);
    }

    /**
     * 导出订单
     */
    @ApiOperation(value = "导出订单")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public void export(HttpServletResponse response, OrderListParam params) throws Exception {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        params.setPage(Constants.PAGE_NUMBER);
        params.setPageSize(Constants.MAX_ROWS);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<UserOrderDto> result = orderService.getUserOrderList(params);

        // excel标题
        String[] title = { "订单号", "会员名称", "手机号", "订单类型", "所属门店", "总金额", "支付状态", "订单状态" };

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
                String mobile = "";
                if (orderDto.getStoreInfo() != null) {
                    storeName = orderDto.getStoreInfo().getName();
                }
                if (orderDto.getUserInfo() != null) {
                    userName = orderDto.getUserInfo().getName();
                }
                if (orderDto.getAddress() != null) {
                    mobile = orderDto.getAddress().getMobile();
                } else if(orderDto.getUserInfo() != null) {
                    mobile = orderDto.getUserInfo().getMobile();
                }
                content[i][0] = objectConvertToString(orderDto.getOrderSn());
                content[i][1] = objectConvertToString(userName);
                content[i][2] = objectConvertToString(mobile);
                content[i][3] = objectConvertToString(orderDto.getTypeName());
                content[i][4] = objectConvertToString(storeName);
                content[i][5] = objectConvertToString(orderDto.getAmount());
                content[i][6] = objectConvertToString(orderDto.getPayStatus());
                content[i][7] = objectConvertToString(orderDto.getStatusText());
            }
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        ExcelUtil.setResponseHeader(response, fileName, wb);

        logger.info("导出订单成功...");
    }
}
