package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtUserTagRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员标签规则Mapper接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserTagRuleMapper extends BaseMapper<MtUserTagRule> {

    /**
     * 获取商户的规则列表
     *
     * @param merchantId 商户ID
     * @param status 状态
     * @return
     */
    List<MtUserTagRule> getMerchantRuleList(@Param("merchantId") Integer merchantId, @Param("status") String status);

    /**
     * 获取自动执行的规则列表
     *
     * @param merchantId 商户ID
     * @return
     */
    List<MtUserTagRule> getAutoRuleList(@Param("merchantId") Integer merchantId);

    /**
     * 根据标签ID获取规则
     *
     * @param tagId 标签ID
     * @return
     */
    List<MtUserTagRule> getRulesByTagId(@Param("tagId") Integer tagId);
}
