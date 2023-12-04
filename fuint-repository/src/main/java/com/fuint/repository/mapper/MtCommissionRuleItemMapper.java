package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtCommissionRuleItem;
import org.apache.ibatis.annotations.Param;
import java.util.Date;

/**
 *  分佣提成规则项目 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtCommissionRuleItemMapper extends BaseMapper<MtCommissionRuleItem> {
    Boolean deleteByRuleId(@Param("ruleId") Integer ruleId, @Param("updateTime") Date updateTime);
}
