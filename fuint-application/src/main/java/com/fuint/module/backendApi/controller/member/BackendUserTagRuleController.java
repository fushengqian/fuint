package com.fuint.module.backendApi.controller.member;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.TagRuleOperatorEnum;
import com.fuint.common.enums.TagRuleTimeRangeEnum;
import com.fuint.common.enums.TagRuleTypeEnum;
import com.fuint.common.service.UserTagRuleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserTagRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台会员标签规则管理控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="后台-会员标签规则管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userTagRule")
public class BackendUserTagRuleController extends BaseController {

    private UserTagRuleService userTagRuleService;

    @Operation(summary="规则列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:userTag:index')")
    public ResponseObject list() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();

        List<MtUserTagRule> ruleList = userTagRuleService.getMerchantRuleList(merchantId, StatusEnum.ENABLED.getKey());

        Map<String, Object> data = new HashMap<>();
        data.put("list", ruleList);
        data.put("ruleTypes", getRuleTypes());
        data.put("operators", getOperators());
        data.put("timeRanges", getTimeRanges());

        return getSuccessResult(data);
    }

    @Operation(summary="保存规则")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:userTag:index')")
    public ResponseObject save(@RequestBody MtUserTagRule rule) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        rule.setMerchantId(merchantId);
        rule.setOperator(operator);

        if (rule.getId() != null && rule.getId() > 0) {
            userTagRuleService.updateRule(rule, accountInfo);
        } else {
            userTagRuleService.addRule(rule, merchantId);
        }

        return getSuccessResult(true);
    }

    @Operation(summary="删除规则")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:userTag:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        userTagRuleService.deleteRule(id, accountInfo);

        return getSuccessResult(true);
    }

    @Operation(summary="手动执行规则")
    @RequestMapping(value = "/execute/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:userTag:index')")
    public ResponseObject execute(@PathVariable("id") Integer id) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();

        if (merchantId == null || merchantId <= 0) {
            return getFailureResult(1002, "您的帐号不是商户，没有操作权限");
        }

        // 执行单个规则
        userTagRuleService.executeRules(id, accountInfo);

        return getSuccessResult(true);
    }

    private List<Map<String, String>> getRuleTypes() {
        List<Map<String, String>> list = new ArrayList<>();
        for (TagRuleTypeEnum item : TagRuleTypeEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("key", item.getKey());
            map.put("value", item.getValue());
            list.add(map);
        }
        return list;
    }

    private List<Map<String, String>> getOperators() {
        List<Map<String, String>> list = new ArrayList<>();
        for (TagRuleOperatorEnum item : TagRuleOperatorEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("key", item.getKey());
            map.put("value", item.getValue());
            list.add(map);
        }
        return list;
    }

    private List<Map<String, String>> getTimeRanges() {
        List<Map<String, String>> list = new ArrayList<>();
        for (TagRuleTimeRangeEnum item : TagRuleTimeRangeEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("key", item.getKey());
            map.put("value", item.getValue());
            list.add(map);
        }
        return list;
    }
}
