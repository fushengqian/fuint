package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.PrinterService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtPrinter;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打印机管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-打印机相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/printer")
public class BackendPrinterController extends BaseController {

    /**
     * 打印机服务接口
     */
    private PrinterService printerService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 打印机列表查询
     *
     * @param  request HttpServletRequest对象
     * @return 打印机列表
     */
    @ApiOperation(value = "打印机列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String sn = request.getParameter("sn");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId = accountInfo.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(sn)) {
            params.put("sn", sn);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtPrinter> paginationResponse = printerService.queryPrinterListByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新打印机状态
     *
     * @return
     */
    @ApiOperation(value = "更新打印机状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtPrinter mtPrinter = printerService.queryPrinterById(id);
        if (mtPrinter == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();
        mtPrinter.setOperator(operator);
        mtPrinter.setStatus(status);
        printerService.updatePrinter(mtPrinter);

        return getSuccessResult(true);
    }

    /**
     * 保存打印机
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存打印机")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();
        String sn = params.get("sn") == null ? "" : params.get("sn").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String autoPrint = params.get("autoPrint") == null ? YesOrNoEnum.NO.getKey() : params.get("autoPrint").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtPrinter mtPrinter = new MtPrinter();
        mtPrinter.setOperator(accountInfo.getAccountName());
        mtPrinter.setStatus(status);
        mtPrinter.setStoreId(Integer.parseInt(storeId));
        mtPrinter.setName(name);
        mtPrinter.setSn(sn);
        mtPrinter.setAutoPrint(autoPrint);
        mtPrinter.setDescription(description);
        mtPrinter.setMerchantId(accountInfo.getMerchantId());
        if (StringUtil.isNotEmpty(id)) {
            mtPrinter.setId(Integer.parseInt(id));
            printerService.updatePrinter(mtPrinter);
        } else {
            printerService.addPrinter(mtPrinter);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取打印机详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取打印机详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtPrinter printerInfo = printerService.queryPrinterById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !accountInfo.getMerchantId().equals(printerInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("printerInfo", printerInfo);

        return getSuccessResult(result);
    }

    /**
     * 获取打印设置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取打印设置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:setting')")
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.PRINTER.getKey());

        String userName = "";
        String userKey = "";
        String enable = "";
        for (MtSetting setting : settingList) {
            if (StringUtil.isNotEmpty(setting.getValue())) {
                if (setting.getName().equals(PrinterSettingEnum.USER_NAME.getKey())) {
                    userName = setting.getValue();
                } else if (setting.getName().equals(PrinterSettingEnum.USER_KEY.getKey())) {
                    userKey = setting.getValue();
                } else if (setting.getName().equals(PrinterSettingEnum.ENABLE.getKey())) {
                    enable = setting.getValue();
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("userKey", userKey);
        result.put("enable", enable);

        return getSuccessResult(result);
    }

    /**
     * 保存打印设置
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存打印设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:setting')")
    public ResponseObject saveSetting(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userName = param.get("userName") != null ? param.get("userName").toString() : null;
        String userKey = param.get("userKey") != null ? param.get("userKey").toString() : null;
        String enable = param.get("enable") != null ? param.get("enable").toString() : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        PrinterSettingEnum[] settingList = PrinterSettingEnum.values();
        for (PrinterSettingEnum setting : settingList) {
            MtSetting mtSetting = new MtSetting();
            mtSetting.setType(SettingTypeEnum.PRINTER.getKey());
            mtSetting.setName(setting.getKey());
            if (setting.getKey().equals(PrinterSettingEnum.USER_NAME.getKey())) {
                mtSetting.setValue(userName);
            } else if (setting.getKey().equals(PrinterSettingEnum.USER_KEY.getKey())) {
                mtSetting.setValue(userKey);
            } else if (setting.getKey().equals(PrinterSettingEnum.ENABLE.getKey())) {
                mtSetting.setValue(enable);
            }
            mtSetting.setDescription(setting.getValue());
            mtSetting.setOperator(accountInfo.getAccountName());
            mtSetting.setUpdateTime(new Date());
            mtSetting.setMerchantId(accountInfo.getMerchantId());
            mtSetting.setStoreId(0);
            settingService.saveSetting(mtSetting);
        }

        return getSuccessResult(true);
    }

    /**
     * 打印订单
     *
     * @param orderId
     * @return
     */
    @ApiOperation(value = "打印订单")
    @RequestMapping(value = "/doPrint/{orderId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public ResponseObject doPrint(HttpServletRequest request, @PathVariable("orderId") Integer orderId) throws Exception {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null) {
            return getFailureResult(201, "该订单不存在");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !accountInfo.getMerchantId().equals(orderInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        // 打印订单
        Boolean result = printerService.printOrder(orderInfo, false);

        return getSuccessResult(result);
    }
}
