package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.PrinterSettingEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.PrinterPage;
import com.fuint.common.param.PrinterParam;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.PrinterService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtPrinter;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     */
    @ApiOperation(value = "打印机列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject list(@ModelAttribute PrinterPage printerPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            printerPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            printerPage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<MtPrinter> paginationResponse = printerService.queryPrinterListByPagination(printerPage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新打印机状态
     */
    @ApiOperation(value = "更新打印机状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtPrinter mtPrinter = printerService.queryPrinterById(id);
        if (mtPrinter == null) {
            return getFailureResult(201);
        }

        mtPrinter.setOperator(accountInfo.getAccountName());
        mtPrinter.setStatus(status);
        printerService.updatePrinter(mtPrinter);

        return getSuccessResult(true);
    }

    /**
     * 保存打印机
     */
    @ApiOperation(value = "保存打印机")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject saveHandler(@RequestBody PrinterParam printer) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtPrinter mtPrinter = new MtPrinter();
        BeanUtils.copyProperties(printer, mtPrinter);
        mtPrinter.setOperator(accountInfo.getAccountName());
        mtPrinter.setMerchantId(accountInfo.getMerchantId());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            mtPrinter.setStoreId(accountInfo.getStoreId());
        }

        if (printer.getId() != null && printer.getId() > 0) {
            mtPrinter.setId(printer.getId());
            printerService.updatePrinter(mtPrinter);
        } else {
            printerService.addPrinter(mtPrinter);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取打印机详情
     */
    @ApiOperation(value = "获取打印机详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
     */
    @ApiOperation(value = "获取打印设置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:setting')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
     */
    @ApiOperation(value = "保存打印设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('printer:setting')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String userName = param.get("userName") != null ? param.get("userName").toString() : null;
        String userKey = param.get("userKey") != null ? param.get("userKey").toString() : null;
        String enable = param.get("enable") != null ? param.get("enable").toString() : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
     */
    @ApiOperation(value = "打印订单")
    @RequestMapping(value = "/doPrint/{orderId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('order:index')")
    public ResponseObject doPrint(@PathVariable("orderId") Integer orderId) throws Exception {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
