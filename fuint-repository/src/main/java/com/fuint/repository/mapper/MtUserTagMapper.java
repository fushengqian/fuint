package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtUserTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员标签Mapper接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserTagMapper extends BaseMapper<MtUserTag> {

    /**
     * 获取商户标签列表
     *
     * @param merchantId 商户ID
     * @param status 状态
     * @return
     */
    List<MtUserTag> getMerchantTagList(@Param("merchantId") Integer merchantId, @Param("status") String status);

    /**
     * 根据名称查询标签
     *
     * @param merchantId 商户ID
     * @param name 标签名称
     * @return
     */
    MtUserTag getTagByName(@Param("merchantId") Integer merchantId, @Param("name") String name);
}
