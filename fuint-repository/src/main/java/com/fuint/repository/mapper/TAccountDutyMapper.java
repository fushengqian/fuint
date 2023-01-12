package com.fuint.repository.mapper;

import com.fuint.repository.model.TAccountDuty;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 *  后台账号角色 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface TAccountDutyMapper extends BaseMapper<TAccountDuty> {

   List<Long> getDutyIdsByAccountId(long accountId);

   void deleteDutiesByAccountId(long accountId);

}
