package com.fuint.module.clientApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.dto.content.ServiceDto;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtMerchantMapper;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 会员端-服务相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-服务相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/service")
public class ClientServiceController extends BaseController {

    private SettingService settingService;

    private MtMerchantMapper mtMerchantMapper;

    /**
     * 获取我的服务列表
     */
    @ApiOperation(value="获取我的服务列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        Integer merchantId = 0;
        if (StringUtil.isNotEmpty(merchantNo)) {
            MtMerchant mtMerchant = mtMerchantMapper.queryMerchantByNo(merchantNo);
            if (mtMerchant != null) {
                merchantId = mtMerchant.getId();
            }
        }

        MtSetting mtSetting = settingService.querySettingByName(merchantId, storeId, SettingTypeEnum.SERVICES.getKey(), "service_list");

        List<ServiceDto> serviceList = new ArrayList<>();
        if (mtSetting != null && StringUtil.isNotBlank(mtSetting.getValue())) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                serviceList = objectMapper.readValue(mtSetting.getValue(), new TypeReference<List<ServiceDto>>() {});
                // 过滤出启用状态的服务
                serviceList.removeIf(item -> !"A".equals(item.getStatus()));
            } catch (JsonProcessingException e) {
                serviceList = getDefaultServiceList();
            }
        }

        // 如果后台未配置服务，返回默认服务列表
        if (serviceList.isEmpty()) {
            serviceList = getDefaultServiceList();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("serviceList", serviceList);

        return getSuccessResult(result);
    }

    /**
     * 获取默认服务列表（兼容旧版硬编码数据）
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
