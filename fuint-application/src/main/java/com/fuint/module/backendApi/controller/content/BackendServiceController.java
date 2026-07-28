package com.fuint.module.backendApi.controller.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.dto.content.ServiceDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 服务管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-服务管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/service")
public class BackendServiceController extends BaseController {

    private SettingService settingService;

    /**
     * 获取服务配置
     */
    @ApiOperation(value = "获取服务配置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:service:index')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();

        MtSetting mtSetting = settingService.querySettingByName(merchantId, storeId, SettingTypeEnum.SERVICES.getKey(), "service_list");

        List<ServiceDto> serviceList = new ArrayList<>();
        if (mtSetting != null && StringUtil.isNotBlank(mtSetting.getValue())) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                serviceList = objectMapper.readValue(mtSetting.getValue(), new TypeReference<List<ServiceDto>>() {});
            } catch (JsonProcessingException e) {
                serviceList = getDefaultServiceList();
            }
        }

        // 如果没有配置，返回默认服务列表
        if (serviceList.isEmpty()) {
            serviceList = getDefaultServiceList();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("serviceList", serviceList);

        return getSuccessResult(result);
    }

    /**
     * 保存服务配置
     */
    @ApiOperation(value = "保存服务配置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:service:edit')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();

        Object serviceListObj = params.get("serviceList");
        if (serviceListObj == null) {
            return getFailureResult(201, "服务列表不能为空");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(serviceListObj);

            MtSetting mtSetting = new MtSetting();
            mtSetting.setMerchantId(merchantId);
            mtSetting.setStoreId(storeId);
            mtSetting.setType(SettingTypeEnum.SERVICES.getKey());
            mtSetting.setName("service_list");
            mtSetting.setValue(jsonValue);
            mtSetting.setDescription("会员端我的服务列表配置");
            mtSetting.setOperator(accountInfo.getAccountName());
            mtSetting.setStatus(StatusEnum.ENABLED.getKey());
            mtSetting.setUpdateTime(new Date());

            settingService.saveSetting(mtSetting);

            return getSuccessResult(true);
        } catch (JsonProcessingException e) {
            return getFailureResult(202, "数据格式错误");
        }
    }

    /**
     * 获取默认服务列表
     */
    private List<ServiceDto> getDefaultServiceList() {
        List<ServiceDto> defaultList = new ArrayList<>();
        String[][] defaults = {
            {"myCoupon", "卡券兑换", "youhuiquan", "link", "subPages/coupon/receive", ""},
            {"coupon", "转赠记录", "lingquan", "link", "pages/give/index", ""},
            {"points", "我的积分", "jifen", "link", "pages/points/detail", ""},
            {"book", "我的预约", "tuxingyanzhengma", "link", "subPages/book/my", ""},
            {"help", "我的帮助", "bangzhu", "link", "pages/help/index", ""},
            {"contact", "在线客服", "kefu", "button", "", "contact"},
            {"address", "收货地址", "shouhuodizhi", "link", "pages/address/index", ""},
            {"refund", "售后服务", "shouhou", "link", "pages/refund/index", ""},
            {"setting", "个人信息", "shezhi1", "link", "pages/user/setting", ""},
            {"book", "立即预约", "naozhong", "link", "subPages/book/index", ""},
            {"commission", "分佣提成", "zijinmingxi", "link", "subPages/commission/statistics", ""},
        };
        for (int i = 0; i < defaults.length; i++) {
            ServiceDto dto = new ServiceDto();
            dto.setId(i + 1);
            dto.setName(defaults[i][1]);
            dto.setIcon(defaults[i][2]);
            dto.setType(defaults[i][3]);
            dto.setUrl(defaults[i][4]);
            dto.setOpenType(defaults[i][5]);
            dto.setSort(i + 1);
            dto.setStatus("A");
            defaultList.add(dto);
        }
        return defaultList;
    }
}
