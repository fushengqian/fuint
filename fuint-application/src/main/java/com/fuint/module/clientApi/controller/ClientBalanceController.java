package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 余额接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-余额相关接口")
@RestController
@RequestMapping(value = "/clientApi/balance")
public class ClientBalanceController extends BaseController {

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 余额服务接口
     * */
    @Autowired
    private BalanceService balanceService;

    /**
     * 支付服务接口
     * */
    @Autowired
    private PaymentService paymentService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 充值配置
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> outParams = new HashMap<>();

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.BALANCE.getKey());

        List<RechargeRuleDto> rechargeRuleList = new ArrayList<>();
        String status = StatusEnum.DISABLE.getKey();
        String remark = "";

        if (settingList.size() > 0) {
            for (MtSetting setting : settingList) {
                if (setting.getName().equals(BalanceSettingEnum.RECHARGE_RULE.getKey())) {
                    status = setting.getStatus();
                    String item[] = setting.getValue().split(",");
                    if (item.length > 0) {
                        for (String value : item) {
                            String el[] = value.split("_");
                            if (el.length == 2) {
                                RechargeRuleDto e = new RechargeRuleDto();
                                e.setRechargeAmount(el[0]);
                                e.setGiveAmount(el[1]);
                                rechargeRuleList.add(e);
                            }
                        }
                    }
                } else if(setting.getName().equals(BalanceSettingEnum.RECHARGE_REMARK.getKey())) {
                    remark = setting.getValue();
                }
            }
        }

        outParams.put("planList", rechargeRuleList);
        if (status.equals(StatusEnum.ENABLED.getKey())) {
            outParams.put("isOpen", true);
        } else {
            outParams.put("isOpen", false);
        }
        outParams.put("remark", remark);

        return getSuccessResult(outParams);
    }

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");

        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001);
        }

        String rechargeAmount = param.get("rechargeAmount") == null ? "" : param.get("rechargeAmount").toString();
        String customAmount = param.get("customAmount") == null ? "" : param.get("customAmount").toString();
        if (StringUtil.isEmpty(rechargeAmount) && StringUtil.isEmpty(customAmount)) {
            return getFailureResult(2000, "请确认充值金额");
        }

        // 充值赠送金额
        String ruleParam = "";
        MtSetting mtSetting = settingService.querySettingByName(BalanceSettingEnum.RECHARGE_RULE.getKey());
        if (StringUtil.isNotEmpty(rechargeAmount) && mtSetting != null) {
            if (mtSetting.getValue() != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
                String rules[] = mtSetting.getValue().split(",");
                for (String rule : rules) {
                     String amountArr[] = rule.split("_");
                     if (amountArr.length == 2) {
                         if (amountArr[0].equals(rechargeAmount)) {
                             ruleParam = rule;
                             break;
                         }
                     }
                }
            }
        }

        // 自定义充值没有赠送金额
        if (StringUtil.isEmpty(rechargeAmount)) {
            rechargeAmount = customAmount;
            ruleParam = customAmount + "_0";
        }

        if (StringUtil.isEmpty(ruleParam)) {
            ruleParam = rechargeAmount + "_0";
        }

        BigDecimal amount = new BigDecimal(rechargeAmount);

        OrderDto orderDto = new OrderDto();
        orderDto.setType(OrderTypeEnum.RECHARGE.getKey());
        orderDto.setUserId(userInfo.getId());
        orderDto.setStoreId(storeId);
        orderDto.setAmount(amount);
        orderDto.setUsePoint(0);
        orderDto.setRemark("会员充值");
        orderDto.setParam(ruleParam);
        orderDto.setStatus(OrderStatusEnum.CREATED.getKey());
        orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setOrderMode("");
        orderDto.setCouponId(0);

        MtOrder orderInfo = orderService.saveOrder(orderDto);

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal pay = amount.multiply(new BigDecimal("100"));
        orderInfo.setPayType(PayTypeEnum.JSAPI.getKey());
        ResponseObject paymentInfo = paymentService.createPrepayOrder(mtUser, orderInfo, (pay.intValue()), "", 0, ip, platform);
        if (paymentInfo.getData() == null) {
            return getFailureResult(201, "抱歉，发起支付失败");
        }

        Object payment = paymentInfo.getData();

        Map<String, Object> data = new HashMap();
        data.put("payment", payment);
        data.put("orderInfo", orderInfo);

        return getSuccessResult(data);
    }

    /**
     * 余额明细
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("user_id", mtUser.getId());
        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"createTime desc", "status asc"});
        PaginationResponse<BalanceDto> paginationResponse = balanceService.queryBalanceListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", paginationResponse);

        return getSuccessResult(result);
    }
}
