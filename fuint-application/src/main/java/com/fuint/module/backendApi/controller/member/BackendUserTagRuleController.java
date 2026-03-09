package com.fuint.module.backendApi.controller.member;

import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.TagRuleOperatorEnum;
import com.fuint.common.enums.TagRuleTimeRangeEnum;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.TagRuleTypeEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.UserTagRuleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserTagRule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 后台会员标签规则管理控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "后台-会员标签规则管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userTagRule")
public class BackendUserTagRuleController extends BaseController {

    private UserTagRuleService userTagRuleService;

    private MerchantService merchantService;

    @ApiOperation(value = "规则列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tagRule:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer merchantId = merchantService.getMerchantId(token);

        List<MtUserTagRule> ruleList = userTagRuleService.getMerchantRuleList(merchantId, StatusEnum.ENABLED.getKey());

        Map<String, Object> data = new HashMap<>();
        data.put("list", ruleList);
        data.put("ruleTypes", getRuleTypes());
        data.put("operators", getOperators());
        data.put("timeRanges", getTimeRanges());

        return getSuccessResult(data);
    }

    @ApiOperation(value = "保存规则")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tagRule:edit')")
    public ResponseObject save(@RequestBody MtUserTagRule rule, HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer merchantId = merchantService.getMerchantId(token);
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        rule.setMerchantId(merchantId);
        rule.setOperator(operator);

        if (rule.getId() != null && rule.getId() > 0) {
            userTagRuleService.updateRule(rule);
        } else {
            userTagRuleService.addRule(rule);
        }

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除规则")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tagRule:delete')")
    public ResponseObject delete(@PathVariable("id") Integer id, HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        userTagRuleService.deleteRule(id, operator);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "手动执行规则")
    @RequestMapping(value = "/execute/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tagRule:edit')")
    public ResponseObject execute(@PathVariable("id") Integer id) {
        // 执行单个规则
        MtUserTagRule rule = userTagRuleService.getById(id);
        if (rule != null) {
            userTagRuleService.batchExecuteRules(rule.getMerchantId());
        }

        return getSuccessResult(true);
    }

    @ApiOperation(value = "批量执行所有规则")
    @RequestMapping(value = "/batchExecute", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tagRule:edit')")
    public ResponseObject batchExecute(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        Integer merchantId = merchantService.getMerchantId(token);

        userTagRuleService.batchExecuteRules(merchantId);

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
