package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.dto.RechargeRuleDto;
import com.fuint.common.enums.BalanceSettingEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BalancePage;
import com.fuint.common.service.BalanceService;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBalance;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 余额管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/balance")
public class BackendBalanceController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 余额明细列表查询
     */
    @ApiOperation(value = "余额明细列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:list')")
    public ResponseObject list(@ModelAttribute BalancePage balancePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            balancePage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            balancePage.setMerchantId(accountInfo.getMerchantId());
        }

        PaginationResponse<BalanceDto> paginationResponse = balanceService.queryBalanceListByPagination(balancePage);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        return getSuccessResult(result);
    }

    /**
     * 提交充值（单个会员）
     */
    @ApiOperation(value = "提交充值")
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:modify')")
    public ResponseObject doRecharge(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String amount = param.get("amount") == null ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "后台充值" : param.get("remark").toString();
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        Integer type = param.get("type") == null ? 1 : Integer.parseInt(param.get("type").toString());// 1 增加，2 扣减
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (!CommonUtil.isNumeric(amount)) {
            return getFailureResult(201, "充值金额必须是数字");
        }
        if (userId < 1) {
            return getFailureResult(201, "充值会员信息不能为空");
        }

        MtBalance mtBalance = new MtBalance();
        MtUser userInfo = memberService.queryMemberById(userId);

        // 扣减余额
        if (type == 2) {
            if (userInfo.getBalance().compareTo(new BigDecimal(amount)) < 0) {
                return getFailureResult(201, "操作失败，会员余额不足");
            }
            mtBalance.setAmount(new BigDecimal(amount).subtract(new BigDecimal(amount).multiply(new BigDecimal("2"))));
        } else {
            mtBalance.setAmount(new BigDecimal(amount));
        }
        mtBalance.setMerchantId(accountInfo.getMerchantId());
        mtBalance.setStoreId(accountInfo.getStoreId());
        mtBalance.setDescription(remark);
        mtBalance.setUserId(userId);
        mtBalance.setOperator(accountInfo.getAccountName());

        balanceService.addBalance(mtBalance, true);
        return getSuccessResult(true);
    }

    /**
     * 发放余额
     */
    @ApiOperation(value = "发放余额")
    @RequestMapping(value = "/distribute", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:distribute')")
    public ResponseObject distribute(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String amount = param.get("amount") == null ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "后台充值" : param.get("remark").toString();
        String userIds = param.get("userIds") == null ? "" : param.get("userIds").toString();
        String object = param.get("object") == null ? "" : param.get("object").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        balanceService.distribute(accountInfo, object, userIds, amount, remark);
        return getSuccessResult(true);
    }

    /**
     * 充值设置详情
     */
    @ApiOperation(value = "充值设置详情")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:setting')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.BALANCE.getKey());

        List<RechargeRuleDto> rechargeRuleList = new ArrayList<>();
        String remark = "";
        String status = "";
        if (settingList.size() > 0) {
            for (MtSetting setting : settingList) {
                 if (setting.getName().equals(BalanceSettingEnum.RECHARGE_RULE.getKey())) {
                     status = setting.getStatus();
                     String item[] = setting.getValue().split(",");
                     if (item.length > 0) {
                         for (String value : item) {
                              String el[] = value.split("_");
                              if (el.length >= 2) {
                                  RechargeRuleDto ruleDto = new RechargeRuleDto();
                                  ruleDto.setRechargeAmount(el[0]);
                                  ruleDto.setGiveAmount(el[1]);
                                  if (el.length >= 3) {
                                      ruleDto.setGiveCouponIds(el[2]);
                                  }
                                  rechargeRuleList.add(ruleDto);
                              }
                         }
                     }
                 } else if(setting.getName().equals(BalanceSettingEnum.RECHARGE_REMARK.getKey())) {
                     remark = setting.getValue();
                 }
            }
        }

        Map<String, Object> result = new HashMap();
        result.put("rechargeRuleList", rechargeRuleList);
        result.put("remark", remark);
        result.put("status", status);

        return getSuccessResult(result);
    }

    /**
     * 保存充值设置
     */
    @ApiOperation(value = "保存充值设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:setting')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        List<LinkedHashMap> rechargeItems = (List) param.get("rechargeItem");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }
        if (rechargeItems.size() < 0) {
            return getFailureResult(201, "充值规则设置不能为空");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        String rechargeRule = "";
        List<String> amounts = new ArrayList<>();
        for (LinkedHashMap item : rechargeItems) {
             String amount = item.get("rechargeAmount").toString();
             String giveCouponIds = item.get("giveCouponIds") == null ? "" : item.get("giveCouponIds").toString();
             if (StringUtil.isNotBlank(giveCouponIds)) {
                 String[] couponIds = giveCouponIds.split("\\|");
                 for (int i = 0; i < couponIds.length; i++) {
                      MtCoupon mtCoupon = couponService.queryCouponById(Integer.parseInt(couponIds[i]));
                      if (mtCoupon == null) {
                          return getFailureResult(201, "赠送卡券ID:"+couponIds[i]+"不存在，请核实！");
                      }
                 }
             }
             if (amounts.contains(amount)) {
                 return getFailureResult(201, "充值金额设置不能有重复");
             }
             if (rechargeRule.length() == 0) {
                 rechargeRule = item.get("rechargeAmount").toString() + '_' + item.get("giveAmount").toString();
             } else {
                 rechargeRule = rechargeRule + ',' + item.get("rechargeAmount").toString() + '_' + item.get("giveAmount").toString();
             }
             if (StringUtil.isNotBlank(giveCouponIds)) {
                 rechargeRule = rechargeRule + '_' + giveCouponIds;
             }
             amounts.add(amount);
        }

        MtSetting setting = new MtSetting();
        setting.setMerchantId(accountInfo.getMerchantId());
        setting.setType(SettingTypeEnum.BALANCE.getKey());
        setting.setName(BalanceSettingEnum.RECHARGE_RULE.getKey());
        setting.setValue(rechargeRule);
        setting.setDescription(BalanceSettingEnum.RECHARGE_RULE.getValue());
        setting.setStatus(status);
        setting.setOperator(accountInfo.getAccountName());
        setting.setUpdateTime(new Date());
        settingService.saveSetting(setting);

        // 保存充值说明
        MtSetting settingRemark = new MtSetting();
        settingRemark.setMerchantId(accountInfo.getMerchantId());
        settingRemark.setType(SettingTypeEnum.BALANCE.getKey());
        settingRemark.setName(BalanceSettingEnum.RECHARGE_REMARK.getKey());
        settingRemark.setValue(remark);
        settingRemark.setDescription("");
        settingRemark.setStatus(status);
        settingRemark.setOperator(accountInfo.getAccountName());
        settingRemark.setUpdateTime(new Date());
        settingService.saveSetting(settingRemark);

        return getSuccessResult(true);
    }
}
