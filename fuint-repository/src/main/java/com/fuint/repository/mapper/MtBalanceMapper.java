package com.fuint.repository.mapper;

import com.fuint.repository.model.MtBalance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 余额变化表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtBalanceMapper extends BaseMapper<MtBalance> {

    List<MtBalance> getBalanceListByOrderSn(@Param("orderSn") String orderSn);

}
