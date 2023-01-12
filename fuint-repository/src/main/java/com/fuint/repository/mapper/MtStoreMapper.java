package com.fuint.repository.mapper;

import com.fuint.repository.bean.StoreDistanceBean;
import com.fuint.repository.model.MtStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门店表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtStoreMapper extends BaseMapper<MtStore> {

    MtStore queryStoreByName(@Param("name") String name);

    void resetDefaultStore();

    List<MtStore> findStoresByIds(@Param("ids") List<Integer> ids);

    List<StoreDistanceBean> queryByDistance(@Param("keyword") String keyword, @Param("latitude") String latitude, @Param("longitude") String longitude);

}
