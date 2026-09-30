package com.fuint.common.service.impl;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.ExpressDto;
import com.fuint.common.dto.order.ExpressTraceDto;
import com.fuint.common.dto.order.ExpressTraceResultDto;
import com.fuint.common.dto.order.UserOrderDto;
import com.fuint.common.enums.ExpressCompanyEnum;
import com.fuint.common.enums.Kuaidi100SettingEnum;
import com.fuint.common.enums.OrderModeEnum;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.ExpressService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.KD100Util;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流查询服务实现
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class ExpressServiceImpl implements ExpressService {

    private static final Logger logger = LoggerFactory.getLogger(ExpressServiceImpl.class);

    private OrderService orderService;

    private SettingService settingService;

    @Override
    public ExpressTraceResultDto queryExpressTrace(Integer orderId, UserInfo userInfo) throws BusinessCheckException {
        return doQuery(orderService.getMyOrderById(orderId), userInfo);
    }

    @Override
    public ExpressTraceResultDto queryExpressTraceByOrderSn(String orderSn, UserInfo userInfo) throws BusinessCheckException {
        return doQuery(orderService.getOrderByOrderSn(orderSn), userInfo);
    }

    /**
     * 查询物流轨迹
     *
     * @param orderInfo 订单信息
     * @param userInfo 当前会员
     * @return
     */
    private ExpressTraceResultDto doQuery(UserOrderDto orderInfo, UserInfo userInfo) throws BusinessCheckException {
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new BusinessCheckException("订单不存在");
        }
        if (userInfo != null && userInfo.getId() != null && !userInfo.getId().equals(orderInfo.getUserId())) {
            throw new BusinessCheckException("订单信息有误");
        }
        // 仅配送订单才有物流信息
        if (!OrderModeEnum.EXPRESS.getKey().equals(orderInfo.getOrderMode())) {
            throw new BusinessCheckException("该订单无需物流配送");
        }
        // 已发货之后才能查询物流轨迹
        if (OrderStatusEnum.CREATED.getKey().equals(orderInfo.getStatus())
                || OrderStatusEnum.PAID.getKey().equals(orderInfo.getStatus())
                || OrderStatusEnum.DELIVERY.getKey().equals(orderInfo.getStatus())) {
            throw new BusinessCheckException("该订单还未发货，暂无物流信息");
        }

        ExpressDto expressInfo = orderInfo.getExpressInfo();
        if (expressInfo == null || StringUtil.isEmpty(expressInfo.getExpressNo())) {
            throw new BusinessCheckException("该订单暂未填写物流信息");
        }

        // 物流公司编码，后台填写的可能只有公司名称，这里做一次兜底转换
        String expressCode = expressInfo.getExpressCode();
        if (StringUtil.isEmpty(expressCode) && StringUtil.isNotEmpty(expressInfo.getExpressCompany())) {
            for (ExpressCompanyEnum item : ExpressCompanyEnum.values()) {
                if (item.getKey().equalsIgnoreCase(expressInfo.getExpressCompany()) || item.getValue().equals(expressInfo.getExpressCompany())) {
                    expressCode = item.getKey();
                    break;
                }
            }
        }
        if (StringUtil.isEmpty(expressCode)) {
            throw new BusinessCheckException("暂不支持该物流公司的查询");
        }
        // 商家自送没有第三方物流轨迹
        if (ExpressCompanyEnum.SELF.getKey().equals(expressCode)) {
            throw new BusinessCheckException("商家自送订单，无需查询物流");
        }

        // 快递100配置
        Map<String, String> config = getKuaidi100Config(orderInfo.getMerchantId());
        String customer = config.get(Kuaidi100SettingEnum.CUSTOMER.getKey());
        String secretKey = config.get(Kuaidi100SettingEnum.SECRET_KEY.getKey());
        String enable = config.get(Kuaidi100SettingEnum.ENABLE.getKey());
        // 未配置该项时按启用处理
        if (StringUtil.isNotEmpty(enable) && !StatusEnum.ENABLED.getKey().equals(enable) && !"on".equals(enable)) {
            throw new BusinessCheckException("商家暂未开通物流查询功能");
        }
        if (StringUtil.isEmpty(customer) || StringUtil.isEmpty(secretKey)) {
            throw new BusinessCheckException("商家暂未开通物流查询功能");
        }

        // 顺丰、京东等快递需要校验手机号后四位
        String phone = "";
        if (orderInfo.getAddress() != null && StringUtil.isNotEmpty(orderInfo.getAddress().getMobile())) {
            String mobile = orderInfo.getAddress().getMobile();
            if (mobile.length() >= 4) {
                phone = mobile.substring(mobile.length() - 4);
            }
        }

        logger.info("查询物流信息 orderId={}, expressCode={}, expressNo={}", orderInfo.getId(), expressCode, expressInfo.getExpressNo());
        Map<String, Object> result = KD100Util.queryExpress(expressCode, expressInfo.getExpressNo(), phone, secretKey, customer);

        ExpressTraceResultDto resultDto = new ExpressTraceResultDto();
        resultDto.setOrderId(orderInfo.getId());
        resultDto.setExpressCompany(expressInfo.getExpressCompany());
        resultDto.setExpressCode(expressCode);
        resultDto.setExpressNo(expressInfo.getExpressNo());
        resultDto.setExpressTime(expressInfo.getExpressTime());
        resultDto.setState(result.get("state") == null ? "" : String.valueOf(result.get("state")));
        resultDto.setStateText(getStateText(resultDto.getState()));
        resultDto.setIsCheck("1".equals(String.valueOf(result.get("ischeck"))));
        resultDto.setTraceList(buildTraceList(result.get("data")));

        return resultDto;
    }

    /**
     * 获取快递100配置
     *
     * @param merchantId 商户ID
     * @return
     */
    private Map<String, String> getKuaidi100Config(Integer merchantId) {
        Map<String, String> config = new HashMap<>();
        if (merchantId == null) {
            return config;
        }
        List<MtSetting> settingList = settingService.getSettingList(merchantId, SettingTypeEnum.KUAIDI100.getKey());
        if (settingList == null || settingList.size() < 1) {
            return config;
        }
        for (MtSetting setting : settingList) {
            if (setting.getName() == null || setting.getValue() == null) {
                continue;
            }
            if (setting.getName().equals(Kuaidi100SettingEnum.CUSTOMER.getKey())) {
                config.put(Kuaidi100SettingEnum.CUSTOMER.getKey(), setting.getValue());
            } else if (setting.getName().equals(Kuaidi100SettingEnum.SECRET_KEY.getKey())) {
                config.put(Kuaidi100SettingEnum.SECRET_KEY.getKey(), setting.getValue());
            } else if (setting.getName().equals(Kuaidi100SettingEnum.ENABLE.getKey())) {
                config.put(Kuaidi100SettingEnum.ENABLE.getKey(), setting.getValue());
            }
        }
        return config;
    }

    /**
     * 组装物流轨迹
     *
     * @param data 快递100返回轨迹
     * @return
     */
    private List<ExpressTraceDto> buildTraceList(Object data) {
        List<ExpressTraceDto> traceList = new ArrayList<>();
        if (data == null) {
            return traceList;
        }
        List<Map<String, Object>> list = null;
        try {
            list = (List<Map<String, Object>>) data;
        } catch (Exception e) {
            logger.error("解析物流轨迹失败：{}", e.getMessage());
            return traceList;
        }
        if (list == null || list.size() < 1) {
            return traceList;
        }
        for (Map<String, Object> item : list) {
            ExpressTraceDto traceDto = new ExpressTraceDto();
            traceDto.setTime(item.get("ftime") == null ? "" : String.valueOf(item.get("ftime")));
            traceDto.setFtime(item.get("time") == null ? "" : String.valueOf(item.get("time")));
            traceDto.setContext(item.get("context") == null ? "" : String.valueOf(item.get("context")));
            traceDto.setStatus(item.get("status") == null ? "" : String.valueOf(item.get("status")));
            traceDto.setAreaName(item.get("areaName") == null ? "" : String.valueOf(item.get("areaName")));
            traceDto.setAreaCode(item.get("areaCode") == null ? "" : String.valueOf(item.get("areaCode")));
            traceList.add(traceDto);
        }
        return traceList;
    }

    /**
     * 物流状态说明
     *
     * @param state 状态编码
     * @return
     */
    private String getStateText(String state) {
        if (StringUtil.isEmpty(state)) {
            return "运输中";
        }
        switch (state) {
            case "0":
                return "在途";
            case "1":
                return "揽收";
            case "2":
                return "疑难";
            case "3":
                return "已签收";
            case "4":
                return "退签";
            case "5":
                return "派件中";
            case "6":
                return "退回中";
            case "7":
                return "转投";
            case "8":
                return "清关中";
            case "14":
                return "拒签";
            default:
                return "运输中";
        }
    }
}
