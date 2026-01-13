package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.dto.RechargeRuleDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.BalanceSettingEnum;
import com.fuint.common.enums.PayTypeEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BalanceListParam;
import com.fuint.common.param.BalancePage;
import com.fuint.common.param.RechargeParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtUser;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping(value = "/clientApi/balance")
public class ClientBalanceController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 充值配置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo");

        Map<String, Object> outParams = new HashMap<>();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        List<MtSetting> settingList = settingService.getSettingList(merchantId, SettingTypeEnum.BALANCE.getKey());

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
                            if (el.length >= 2) {
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
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody RechargeParam rechargeParam) throws BusinessCheckException {
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String isWechat = request.getHeader("isWechat") == null ? "" : request.getHeader("isWechat");

        UserInfo userInfo = TokenUtil.getUserInfo();
        if (null == userInfo) {
            return getFailureResult(1001);
        }
        rechargeParam.setMemberId(userInfo.getId());
        MtOrder orderInfo = orderService.doRecharge(request, rechargeParam);

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal pay = orderInfo.getAmount().multiply(new BigDecimal("100"));
        orderInfo.setPayType(PayTypeEnum.JSAPI.getKey());
        ResponseObject paymentInfo = paymentService.createPrepayOrder(mtUser, orderInfo, pay.intValue(), "", 0, ip, platform, isWechat);
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
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody BalanceListParam balanceListParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        Integer page = balanceListParam.getPage() == null ? Constants.PAGE_NUMBER : balanceListParam.getPage();
        Integer pageSize = balanceListParam.getPageSize() == null ? Constants.PAGE_SIZE : balanceListParam.getPageSize();

        BalancePage balancePage = new BalancePage();
        balancePage.setPage(page);
        balancePage.setPageSize(pageSize);
        balancePage.setUserId(userInfo.getId());
        PaginationResponse<BalanceDto> paginationResponse = balanceService.queryBalanceListByPagination(balancePage);

        Map<String, Object> result = new HashMap<>();
        result.put("data", paginationResponse);

        return getSuccessResult(result);
    }
}
