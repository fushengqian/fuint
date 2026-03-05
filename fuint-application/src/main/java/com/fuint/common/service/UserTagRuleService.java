package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserTagRule;

import java.util.List;

/**
 * 会员标签规则服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface UserTagRuleService extends IService<MtUserTagRule> {

    /**
     * 获取商户规则列表
     *
     * @param merchantId 商户ID
     * @param status 状态
     * @return
     */
    List<MtUserTagRule> getMerchantRuleList(Integer merchantId, String status);

    /**
     * 添加规则
     *
     * @param rule 规则信息
     * @return
     * @throws BusinessCheckException
     */
    MtUserTagRule addRule(MtUserTagRule rule) throws BusinessCheckException;

    /**
     * 编辑规则
     *
     * @param rule 规则信息
     * @return
     * @throws BusinessCheckException
     */
    MtUserTagRule updateRule(MtUserTagRule rule) throws BusinessCheckException;

    /**
     * 删除规则
     *
     * @param id 规则ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteRule(Integer id, String operator) throws BusinessCheckException;

    /**
     * 执行单个会员的标签规则
     *
     * @param user 会员信息
     * @param order 订单信息（可选）
     */
    void executeRulesForUser(MtUser user, MtOrder order);

    /**
     * 批量执行标签规则
     *
     * @param merchantId 商户ID
     */
    void batchExecuteRules(Integer merchantId);

    /**
     * 检查会员是否符合规则
     *
     * @param user 会员
     * @param rule 规则
     * @return
     */
    boolean checkUserMatchRule(MtUser user, MtUserTagRule rule);
}
