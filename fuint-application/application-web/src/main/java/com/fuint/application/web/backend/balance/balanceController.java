package com.fuint.application.web.backend.balance;

import com.fuint.application.dao.entities.MtBalance;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.BalanceDto;
import com.fuint.application.dto.RechargeRuleDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.BalanceSettingEnum;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.balance.BalanceService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.ShiroUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 充值管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/balance")
public class balanceController {

    private static final Logger logger = LoggerFactory.getLogger(balanceController.class);

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
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 余额明细列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/balance/list")
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        PaginationResponse<BalanceDto> paginationResponse = balanceService.queryBalanceListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        return "balance/list";
    }

    /**
     * 充值页面
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 充值页面
     */
    @RequiresPermissions("backend/balance/recharge")
    @RequestMapping(value = "/recharge")
    public String recharge(HttpServletRequest request, Model model) throws BusinessCheckException {
        Integer userId = request.getParameter("userId") == null ? 0 : Integer.parseInt(request.getParameter("userId"));

        MtUser userInfo = memberService.queryMemberById(userId);

        model.addAttribute("userInfo", userInfo);

        return "balance/recharge";
    }

    /**
     * 提交充值
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/balance/doRecharge")
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult doRecharge(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String amount = request.getParameter("amount") == null ? "0" : request.getParameter("amount");
        String remark = request.getParameter("remark") == null ? "后台充值" : request.getParameter("remark");
        Integer userId = request.getParameter("userId") == null ? 0 : Integer.parseInt(request.getParameter("userId"));
        Integer type = request.getParameter("type") == null ? 1 : Integer.parseInt(request.getParameter("type"));

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        ReqResult reqResult = new ReqResult();

        if (!CommonUtil.isNumeric(amount)) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值金额必须是数字！");
        }

        if (shiroUser == null) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请重新登录！");
        }

        if (userId < 1) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值会员信息不能为空！");
        }

        String operator = shiroUser.getAcctName();

        MtBalance mtBalance = new MtBalance();

        if (type == 2) {
            // 扣减
            mtBalance.setAmount(new BigDecimal(amount).subtract(new BigDecimal(amount).multiply(new BigDecimal("2"))));
        } else {
            mtBalance.setAmount(new BigDecimal(amount));
        }
        mtBalance.setDescription(remark);
        mtBalance.setUserId(userId);
        mtBalance.setOperator(operator);
        mtBalance.setOrderSn("");

        balanceService.addBalance(mtBalance);

        MtUser userInfo = memberService.queryMemberById(userId);

        reqResult.setResult(true);
        Map<String, Object> data = new HashMap();
        data.put("userInfo", userInfo);
        reqResult.setData(data);

        return reqResult;
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/balance/setting")
    @RequestMapping(value = "/setting")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.BALANCE.getKey());

        List<RechargeRuleDto> rechargeRuleList = new ArrayList<>();

        if (settingList.size() > 0) {
            for (MtSetting setting : settingList) {
                 if (setting.getName().equals(BalanceSettingEnum.RECHARGE_RULE.getKey())) {
                     model.addAttribute("status", setting.getStatus());
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
                 }
            }
        }

        model.addAttribute("rechargeRuleList", rechargeRuleList);

        return "balance/setting";
    }

    /**
     * 提交保存
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/balance/saveSetting")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");
        String rechargeAmountArr[] = request.getParameterValues("rechargeAmount") == null ? new String[0] : request.getParameterValues("rechargeAmount");
        String giveAmountArr[] = request.getParameterValues("giveAmount") == null ? new String[0] : request.getParameterValues("giveAmount");

        ReqResult reqResult = new ReqResult();
        if (rechargeAmountArr.length != giveAmountArr.length || rechargeAmountArr.length == 0) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值规则设置不能为空！");
        }

        boolean isNumeric = true;
        for (String val : rechargeAmountArr) {
             if (!CommonUtil.isNumeric(val)) {
                 isNumeric = false;
                 break;
             }
        }
        for (String val : giveAmountArr) {
            if (!CommonUtil.isNumeric(val)) {
                isNumeric = false;
                break;
            }
        }
        if (!isNumeric) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值金额和赠送金额必须是数字！");
            return reqResult;
        }

        String rechargeRule = "";
        for (int i = 0; i < rechargeAmountArr.length; i++) {
            if (rechargeRule.length() == 0) {
                rechargeRule = rechargeAmountArr[i] + '_' + giveAmountArr[i];
            } else {
                rechargeRule = rechargeRule + ',' +rechargeAmountArr[i] + '_' + giveAmountArr[i];
            }
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        if (StringUtils.isEmpty(operator)) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请重新登录！");
            return reqResult;
        }

        MtSetting setting = new MtSetting();

        setting.setType(SettingTypeEnum.BALANCE.getKey());
        setting.setName(BalanceSettingEnum.RECHARGE_RULE.getKey());
        setting.setValue(rechargeRule);
        setting.setDescription(BalanceSettingEnum.RECHARGE_RULE.getValue());
        setting.setStatus(status);
        setting.setOperator(operator);
        setting.setUpdateTime(new Date());

        settingService.saveSetting(setting);

        reqResult.setResult(true);

        return reqResult;
    }
}
